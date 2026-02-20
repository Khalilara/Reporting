package com.demo.Repository.Channel;

import com.demo.Model.Channel.WeeklyEvolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;  


@Repository
public interface WeeklyEvolutionRepository extends JpaRepository<WeeklyEvolution, Long> {
    
    // Vérifier si un snapshot existe déjà pour une date donnée
    boolean existsByWeekDate(LocalDate weekDate);
    
    // Récupérer tout l'historique trié du plus récent au plus ancien
    List<WeeklyEvolution> findAllByOrderByWeekDateDesc();
    
    // Récupérer les snapshots d'une année spécifique
    List<WeeklyEvolution> findByYearOrderByWeekNumber(Integer year);
    
    // Optionnel : récupérer le dernier snapshot (pour comparaison)
    WeeklyEvolution findFirstByOrderByWeekDateDesc();

    Optional<WeeklyEvolution> findByWeekDate(LocalDate weekDate);
}