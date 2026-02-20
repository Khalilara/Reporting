package com.demo.service.Channel;

import com.demo.Model.Channel.TopReseller;
import com.demo.Model.Channel.WeeklyEvolution;
import com.demo.Repository.Channel.WeeklyEvolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklyEvolutionService {

    @Autowired
    private WeeklyEvolutionRepository evolutionRepository;

    @Autowired
    private DashboardService dashboardService;

    /**
     * Crée un snapshot manuel pour une date donnée avec tous les KPI détaillés
     */
    @Transactional
    public WeeklyEvolution createManualSnapshot(LocalDate weekDate, String customWeekLabel) {
        // 1. Vérifier si la date existe déjà
        if (evolutionRepository.existsByWeekDate(weekDate)) {
            throw new RuntimeException("Un snapshot existe déjà pour la date : " + weekDate);
        }

        // 2. Calculer le numéro de semaine automatique (gardé pour référence)
        WeekFields weekFields = WeekFields.of(Locale.FRANCE);
        int weekNumber = weekDate.get(weekFields.weekOfWeekBasedYear());
        int year = weekDate.getYear();

        // 3. Récupérer les KPI depuis DashboardService
        
        // 3.1 Données SMB et EBT
        Map<String, BigDecimal> smbData = dashboardService.getCaSmb();
        Map<String, BigDecimal> ebtData = dashboardService.getCaEbt();

        // Extraction avec valeurs par défaut
        BigDecimal smbTotal = smbData.getOrDefault("total", BigDecimal.ZERO);
        BigDecimal ebtTotal = ebtData.getOrDefault("total", BigDecimal.ZERO);
        
        BigDecimal smbKnox = smbData.getOrDefault("knoxSw", BigDecimal.ZERO);
        BigDecimal smbService = smbData.getOrDefault("service", BigDecimal.ZERO);
        BigDecimal ebtKnox = ebtData.getOrDefault("knoxSw", BigDecimal.ZERO);
        BigDecimal ebtService = ebtData.getOrDefault("service", BigDecimal.ZERO);

        // 3.2 Calcul des totaux par produit
        BigDecimal knoxTotal = smbKnox.add(ebtKnox);
        BigDecimal serviceTotal = smbService.add(ebtService);
        
        // 3.3 Global revenue
        BigDecimal globalRevenue = smbTotal.add(ebtTotal);

        // 3.4 Breakdown par type de revendeur (via SMB distribution)
        Map<String, Map<String, Object>> resellerDistribution = dashboardService.getSmbDistributionByResellerTypName();
        Map<String, BigDecimal> resellerBreakdown = new LinkedHashMap<>();
        
        for (Map.Entry<String, Map<String, Object>> entry : resellerDistribution.entrySet()) {
            Map<String, Object> stats = entry.getValue();
            BigDecimal revenue = (BigDecimal) stats.getOrDefault("totalRevenue", BigDecimal.ZERO);
            resellerBreakdown.put(entry.getKey(), revenue);
        }

        // 3.5 Données des channels avec targets
        List<Map<String, Object>> channelsData = dashboardService.getRevenueWithTargetsDetailed();

        // 3.6 Calcul des targets globaux
        List<TopReseller> topResellers = dashboardService.getTopResellers();
        BigDecimal totalTarget = topResellers.stream()
                .map(TopReseller::getTarget)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal achievement = totalTarget.compareTo(BigDecimal.ZERO) > 0 
                ? globalRevenue.multiply(BigDecimal.valueOf(100))
                               .divide(totalTarget, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> targetsData = new LinkedHashMap<>();
        targetsData.put("totalRevenue", globalRevenue);
        targetsData.put("totalTarget", totalTarget);
        targetsData.put("achievement", achievement);

        // 4. Créer et remplir le snapshot
        WeeklyEvolution snapshot = new WeeklyEvolution();
        snapshot.setWeekDate(weekDate);
        snapshot.setWeekNumber(weekNumber);
        snapshot.setCustomWeekLabel(customWeekLabel);  // ← Label personnalisé
        snapshot.setYear(year);
        snapshot.setCreatedAt(LocalDateTime.now());
        snapshot.setIsManual(true);
        
        // 4.1 Valeurs simples
        snapshot.setGlobalRevenue(globalRevenue);
        snapshot.setSmbRevenue(smbTotal);
        snapshot.setEbtRevenue(ebtTotal);
        snapshot.setKnoxSwRevenue(knoxTotal);
        snapshot.setServiceRevenue(serviceTotal);
        
        // 4.2 Données structurées (JSON)
        snapshot.setResellerBreakdown(resellerBreakdown);
        snapshot.setChannelsData(channelsData);
        snapshot.setTargetsData(targetsData);

        // 5. Sauvegarder et retourner
        return evolutionRepository.save(snapshot);
    }

    /**
     * Récupère tout l'historique des snapshots
     */
    public List<WeeklyEvolution> getHistory() {
        return evolutionRepository.findAllByOrderByWeekDateDesc();
    }

    /**
     * Récupère les snapshots d'une année spécifique
     */
    public List<WeeklyEvolution> getHistoryByYear(Integer year) {
        return evolutionRepository.findByYearOrderByWeekNumber(year);
    }

    /**
     * Récupère le dernier snapshot créé
     */
    public Optional<WeeklyEvolution> getLatestSnapshot() {
        return Optional.ofNullable(evolutionRepository.findFirstByOrderByWeekDateDesc());
    }

    /**
     * Supprime un snapshot par son ID
     */
    @Transactional
    public void deleteSnapshot(Long id) {
        evolutionRepository.deleteById(id);
    }

    /**
     * Compare deux semaines
     */
    public Map<String, Object> compareWeeks(LocalDate week1, LocalDate week2) {
        Optional<WeeklyEvolution> snap1 = evolutionRepository.findByWeekDate(week1);
        Optional<WeeklyEvolution> snap2 = evolutionRepository.findByWeekDate(week2);
        
        if (snap1.isEmpty() || snap2.isEmpty()) {
            throw new RuntimeException("Une des semaines n'existe pas");
        }
        
        Map<String, Object> comparison = new LinkedHashMap<>();
        comparison.put("week1", snap1.get());
        comparison.put("week2", snap2.get());
        
        BigDecimal evolution = snap2.get().getGlobalRevenue()
                .subtract(snap1.get().getGlobalRevenue());
        BigDecimal percentage = snap1.get().getGlobalRevenue().compareTo(BigDecimal.ZERO) > 0
                ? evolution.multiply(BigDecimal.valueOf(100))
                          .divide(snap1.get().getGlobalRevenue(), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        comparison.put("evolution", evolution);
        comparison.put("evolutionPercentage", percentage);
        
        return comparison;
    }
}