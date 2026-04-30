package com.demo.service.Channel;

import com.demo.DTO.Channel.ChannelRevenueDTO;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Model.Channel.TopReseller;
import com.demo.Model.Channel.SalesDataArchive;
import com.demo.Repository.Channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
public class DashboardService {
    @Autowired
    private SalesDataRepository salesDataRepository;
    @Autowired
    private ResellerCategRepository resellerCategRepository;
    @Autowired
    private CustomerCategRepository customerCategRepository;
    @Autowired
    private ProductCategRepository productCategRepository;
    @Autowired
    private PreparedDataRepository preparedDataRepository;

    @Autowired
    private TopResellerRepository topResellerRepository;
    
    @Autowired
    private SalesDataArchiveRepository salesDataArchiveRepository;

    public void updateTopResellerTarget(String name, BigDecimal target) {
        TopReseller reseller = topResellerRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("TopReseller non trouvé"));

        reseller.setTarget(target);
        topResellerRepository.save(reseller);
    }
    public List<TopReseller> getTopResellers() {
        return topResellerRepository.findAll();
    }
public List<Map<String, Object>> getRevenueWithTargetsDetailed() {
    List<TopReseller> topResellers = topResellerRepository.findAll();
    Map<String, Map<String, Map<String, BigDecimal>>> detailByChannel = new HashMap<>();

    // Initialisation structure channel/type/segment
    for (TopReseller reseller : topResellers) {
        detailByChannel.put(reseller.getName(), new HashMap<>());
        detailByChannel.get(reseller.getName()).put("knoxSW", new HashMap<>());
        detailByChannel.get(reseller.getName()).put("service", new HashMap<>());
        detailByChannel.get(reseller.getName()).get("knoxSW").put("smb", BigDecimal.ZERO);
        detailByChannel.get(reseller.getName()).get("knoxSW").put("ebt", BigDecimal.ZERO);
        detailByChannel.get(reseller.getName()).get("service").put("smb", BigDecimal.ZERO);
        detailByChannel.get(reseller.getName()).get("service").put("ebt", BigDecimal.ZERO);
    }

    // Agrégation
    for (PreparedData salesData : preparedDataRepository.findAll()) {
        String channel = salesData.getChannel();
        String productType = salesData.getProductType();
        String customerType = salesData.getCustomerType();
        BigDecimal revenue = salesData.getRevenue();

        if (channel == null || productType == null || customerType == null) continue;
        if (!detailByChannel.containsKey(channel)) continue;

        String prodKey = productType.equalsIgnoreCase("Knox SW") ? "knoxSW" :
                         productType.equalsIgnoreCase("Service") ? "service" : null;
        String custKey = customerType.equalsIgnoreCase("SMB") ? "smb" :
                         customerType.equalsIgnoreCase("EBT") ? "ebt" : null;

        if (prodKey != null && custKey != null) {
            Map<String, BigDecimal> prodMap = detailByChannel.get(channel).get(prodKey);
            prodMap.put(custKey, prodMap.get(custKey).add(revenue));
        }
    }

    // Construction de la liste avec calcul du total revenue
    List<Map<String, Object>> result = new ArrayList<>();
    for (TopReseller reseller : topResellers) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("channel", reseller.getName());
        BigDecimal totalRevenue = detailByChannel.get(reseller.getName())
                .values().stream()
                .flatMap(m -> m.values().stream())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        entry.put("revenue", totalRevenue);
        entry.put("target", reseller.getTarget());
        entry.put("knoxSW", detailByChannel.get(reseller.getName()).get("knoxSW"));
        entry.put("service", detailByChannel.get(reseller.getName()).get("service"));
        result.add(entry);
    }
    
    // ✅ Trier par revenue décroissant
    result.sort((a, b) -> {
        BigDecimal revA = (BigDecimal) a.get("revenue");
        BigDecimal revB = (BigDecimal) b.get("revenue");
        return revB.compareTo(revA);
    });
    
    return result;
}

    public  Map<String, BigDecimal>  getCaEbt() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal service = BigDecimal.ZERO;
        BigDecimal knoxSw = BigDecimal.ZERO;

        for (PreparedData salesData : preparedDataRepository.findAll()) {
            if ("EBT".equalsIgnoreCase(salesData.getCustomerType())) {
                BigDecimal revenue = salesData.getRevenue();
                total = total.add(revenue);

                String productType = salesData.getProductType();
                if ("Service".equalsIgnoreCase(productType)) {
                    service = service.add(revenue);
                } else if ("Knox SW".equalsIgnoreCase(productType)) {
                    knoxSw = knoxSw.add(revenue);
                }
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("total", total);
        result.put("service", service);
        result.put("knoxSw", knoxSw);
        return result;
    }
    public  Map<String, BigDecimal>  getCaSmb() {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal service = BigDecimal.ZERO;
        BigDecimal knoxSw = BigDecimal.ZERO;

        for (PreparedData salesData : preparedDataRepository.findAll()) {
            if ("SMB".equalsIgnoreCase(salesData.getCustomerType())) {
                BigDecimal revenue = salesData.getRevenue();
                total = total.add(revenue);

                String productType = salesData.getProductType();
                if ("Service".equalsIgnoreCase(productType)) {
                    service = service.add(revenue);
                } else if ("Knox SW".equalsIgnoreCase(productType)) {
                    knoxSw = knoxSw.add(revenue);
                }
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("total", total);
        result.put("service", service);
        result.put("knoxSw", knoxSw);
        return result;
    }


    public Map<String, Map<String, Object>> getSmbDistributionByResellerTypName() {
        Map<String, BigDecimal> totalRevenueByReseller = new LinkedHashMap<>();
        Map<String, BigDecimal> totalKnoxSWByReseller = new LinkedHashMap<>();
        Map<String, BigDecimal> totalServiceByReseller = new LinkedHashMap<>();

        // Étape 1 : collecte des données
        for (PreparedData salesData : preparedDataRepository.findAll()) {
            if (salesData.getCustomerType() != null &&
                    "SMB".equalsIgnoreCase(salesData.getCustomerType().trim()) &&
                    salesData.getResellerTypeName() != null &&
                    salesData.getRevenue() != null &&
                    salesData.getProductType() != null) {

                String resellerType = salesData.getResellerTypeName();
                BigDecimal revenue = salesData.getRevenue();
                String productType = salesData.getProductType().trim();

                // total par type de revendeur
                totalRevenueByReseller.put(
                        resellerType,
                        totalRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );

                // par produit : Knox SW / Service
                if ("Knox SW".equalsIgnoreCase(productType)) {
                    totalKnoxSWByReseller.put(
                            resellerType,
                            totalKnoxSWByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                } else if ("Service".equalsIgnoreCase(productType)) {
                    totalServiceByReseller.put(
                            resellerType,
                            totalServiceByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                }
            }
        }

        // Étape 2 : somme des 3 revenus retenus pour le pourcentage
        BigDecimal localTotalRevenue = totalRevenueByReseller.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (localTotalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyMap();
        }

        // Étape 3 : création du résultat
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        
        // Créer une liste temporaire pour le tri
        List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<>(totalRevenueByReseller.entrySet());
        
        // ✅ Trier par revenue décroissant
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, BigDecimal> entry : sortedEntries) {
            String resellerType = entry.getKey();
            BigDecimal resellerRevenue = entry.getValue();

            BigDecimal percentage = resellerRevenue
                    .multiply(BigDecimal.valueOf(100))
                    .divide(localTotalRevenue, 2, RoundingMode.HALF_UP);

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalRevenue", resellerRevenue);
            stats.put("percentage", percentage);

            Map<String, BigDecimal> productBreakdown = new LinkedHashMap<>();
            productBreakdown.put("Knox SW", totalKnoxSWByReseller.getOrDefault(resellerType, BigDecimal.ZERO));
            productBreakdown.put("Service", totalServiceByReseller.getOrDefault(resellerType, BigDecimal.ZERO));

            stats.put("revenueByProductType", productBreakdown);

            result.put(resellerType, stats);
        }

        return result;
    }
public Map<String, List<BigDecimal>> getRevenueByProductTypePerReseller() {
    Map<String, BigDecimal> totalKnoxSWByReseller = new LinkedHashMap<>();
    Map<String, BigDecimal> totalServiceByReseller = new LinkedHashMap<>();

    List<String> resellerOrder = new ArrayList<>();

    // Étape 1 : collecte des données
    for (PreparedData salesData : preparedDataRepository.findAll()) {
        if (salesData.getCustomerType() != null &&
                "SMB".equalsIgnoreCase(salesData.getCustomerType().trim()) &&
                salesData.getResellerTypeName() != null &&
                salesData.getRevenue() != null &&
                salesData.getProductType() != null) {

            String resellerType = salesData.getResellerTypeName().trim();
            BigDecimal revenue = salesData.getRevenue();
            String productType = salesData.getProductType().trim();

            if (!resellerOrder.contains(resellerType)) {
                resellerOrder.add(resellerType);
            }

            if ("Knox SW".equalsIgnoreCase(productType)) {
                totalKnoxSWByReseller.put(
                        resellerType,
                        totalKnoxSWByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );
            } else if ("Service".equalsIgnoreCase(productType)) {
                totalServiceByReseller.put(
                        resellerType,
                        totalServiceByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );
            }
        }
    }

    // Étape 2 : création du résultat
    Map<String, List<BigDecimal>> result = new LinkedHashMap<>();
    List<BigDecimal> knoxSWList = new ArrayList<>();
    List<BigDecimal> serviceList = new ArrayList<>();

    for (String reseller : resellerOrder) {
        knoxSWList.add(totalKnoxSWByReseller.getOrDefault(reseller, BigDecimal.ZERO));
        serviceList.add(totalServiceByReseller.getOrDefault(reseller, BigDecimal.ZERO));
    }

    result.put("Knox SW", knoxSWList);
    result.put("Service", serviceList);

    return result;
}

    public Map<String, List<PreparedData>> getTopDealsByCustomerType() {
        List<PreparedData> allDeals = preparedDataRepository.findAll();
        
        // Trier tous les deals par revenu décroissant
        allDeals.sort((d1, d2) -> d2.getRevenue().compareTo(d1.getRevenue()));
        
        // Filtrer et limiter à 5 par type de client (inclure même les N/A)
        List<PreparedData> topSMB = allDeals.stream()
                .filter(d -> "SMB".equalsIgnoreCase(d.getCustomerType()))
                .limit(5)
                .collect(Collectors.toList());
        
        List<PreparedData> topEBT = allDeals.stream()
                .filter(d -> "EBT".equalsIgnoreCase(d.getCustomerType()))
                .limit(5)
                .collect(Collectors.toList());
        
        Map<String, List<PreparedData>> result = new HashMap<>();
        result.put("SMB", topSMB);
        result.put("EBT", topEBT);
        
        return result;
    }
    public Map<Integer, Map<String, Map<String, BigDecimal>>> getGlobalRevenueByYear() {
        Map<Integer, Map<String, Map<String, BigDecimal>>> result = new HashMap<>();

        for (PreparedData salesData : preparedDataRepository.findAll()) {
            BigDecimal yearDecimal = salesData.getYear(); // ex: 2025.00
            int year = yearDecimal.intValue(); // conversion en int

            String month = salesData.getMonth(); // ex: "janv.", "févr.", etc.
            BigDecimal revenue = salesData.getRevenue();
            String customerType = salesData.getCustomerType();

            // Initialiser les maps pour l'année si elles n'existent pas
            result.putIfAbsent(year, new HashMap<>());
            Map<String, Map<String, BigDecimal>> yearData = result.get(year);

            yearData.putIfAbsent("Global SMB", new HashMap<>());
            yearData.putIfAbsent("Global EBT", new HashMap<>());
            yearData.putIfAbsent("Global Revenue", new HashMap<>());

            if ("SMB".equals(customerType)) {
                yearData.get("Global SMB").merge(month, revenue, BigDecimal ::add);
            } else {
                yearData.get("Global EBT").merge(month, revenue, BigDecimal ::add);
            }

            yearData.get("Global Revenue").merge(month, revenue, BigDecimal ::add);
        }

        return result;
    }

    public List<String> getChannelResellers() {
        List<String> channels  = new ArrayList<>();
        for (ResellerCateg elmt : resellerCategRepository.findAll()) {
            // Filter out null or empty channel names
            String channel = elmt.getChannel();
            if (channel != null && !channel.isBlank()) {
                channels.add(channel);
            }
        }
        return channels;
    }

    public void createTopReseller(TopReseller topreseller){
        topResellerRepository.save(topreseller);
    }

public Map<String, Object> getRevenueByCustomerTypeAndResellerWithNames() {
    Map<String, BigDecimal> ebtRevenueByReseller = new LinkedHashMap<>();
    Map<String, BigDecimal> smbRevenueByReseller = new LinkedHashMap<>();
    Set<String> resellerOrder = new LinkedHashSet<>();
    BigDecimal globalRevenue = BigDecimal.ZERO;

    // Étape 1 : collecte des données
    for (PreparedData salesData : preparedDataRepository.findAll()) {
        if (salesData.getCustomerType() != null &&
                salesData.getResellerTypeName() != null &&
                salesData.getRevenue() != null) {

            String customerType = salesData.getCustomerType().trim();
            String resellerType = salesData.getResellerTypeName().trim();
            BigDecimal revenue = salesData.getRevenue();

            resellerOrder.add(resellerType);
            globalRevenue = globalRevenue.add(revenue);

            if ("EBT".equalsIgnoreCase(customerType)) {
                ebtRevenueByReseller.put(
                        resellerType,
                        ebtRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );
            } else if ("SMB".equalsIgnoreCase(customerType)) {
                smbRevenueByReseller.put(
                        resellerType,
                        smbRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );
            }
        }
    }

    // Étape 2 : construire des maps avec noms des revendeurs
    Map<String, BigDecimal> ebtMap = new LinkedHashMap<>();
    Map<String, BigDecimal> smbMap = new LinkedHashMap<>();
    Map<String, BigDecimal> totalMap = new LinkedHashMap<>();
    for (String reseller : resellerOrder) {
        BigDecimal ebtVal = ebtRevenueByReseller.getOrDefault(reseller, BigDecimal.ZERO);
        BigDecimal smbVal = smbRevenueByReseller.getOrDefault(reseller, BigDecimal.ZERO);
        ebtMap.put(reseller, ebtVal);
        smbMap.put(reseller, smbVal);
        totalMap.put(reseller, ebtVal.add(smbVal));
    }

    // Étape 3 : construire le résultat final
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("EBT", ebtMap);
    result.put("SMB", smbMap);
    result.put("Total", totalMap);
    result.put("GlobalRevenue", globalRevenue);

    return result;
}

    // ========== ARCHIVE METHOD FOR REVENUE BY CUSTOMER ==========
    public Map<String, Object> getRevenueByCustomerTypeAndResellerWithNamesArchive(Integer year, String quarter, Integer week) {
        Map<String, BigDecimal> ebtRevenueByReseller = new LinkedHashMap<>();
        Map<String, BigDecimal> smbRevenueByReseller = new LinkedHashMap<>();
        Set<String> resellerOrder = new LinkedHashSet<>();
        BigDecimal globalRevenue = BigDecimal.ZERO;

        // Étape 1 : collecte des données depuis archive
        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            if (salesData.getCustomerType() != null &&
                    salesData.getResellerTypeName() != null &&
                    salesData.getRevenue() != null) {

                String customerType = salesData.getCustomerType().trim();
                String resellerType = salesData.getResellerTypeName().trim();
                BigDecimal revenue = salesData.getRevenue();

                resellerOrder.add(resellerType);
                globalRevenue = globalRevenue.add(revenue);

                if ("EBT".equalsIgnoreCase(customerType)) {
                    ebtRevenueByReseller.put(
                            resellerType,
                            ebtRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                } else if ("SMB".equalsIgnoreCase(customerType)) {
                    smbRevenueByReseller.put(
                            resellerType,
                            smbRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                }
            }
        }

        // Étape 2 : construire des maps avec noms des revendeurs
        Map<String, BigDecimal> ebtMap = new LinkedHashMap<>();
        Map<String, BigDecimal> smbMap = new LinkedHashMap<>();
        Map<String, BigDecimal> totalMap = new LinkedHashMap<>();
        for (String reseller : resellerOrder) {
            BigDecimal ebtVal = ebtRevenueByReseller.getOrDefault(reseller, BigDecimal.ZERO);
            BigDecimal smbVal = smbRevenueByReseller.getOrDefault(reseller, BigDecimal.ZERO);
            ebtMap.put(reseller, ebtVal);
            smbMap.put(reseller, smbVal);
            totalMap.put(reseller, ebtVal.add(smbVal));
        }

        // Étape 3 : construire le résultat final
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("EBT", ebtMap);
        result.put("SMB", smbMap);
        result.put("Total", totalMap);
        result.put("GlobalRevenue", globalRevenue);

        return result;
}
    /**
     * Récupère la répartition du CA par productSubSub (sous-produit) pour SMB et EBT
     * Format retourné :
     * {
     *   "SMB": { "productSubSub1": montant, "productSubSub2": montant, ..., "total": montant },
     *   "EBT": { "productSubSub1": montant, "productSubSub2": montant, ..., "total": montant },
     *   "global": { "productSubSub1": montant, "productSubSub2": montant, ..., "total": montant }
     * }
     */
    public Map<String, Map<String, BigDecimal>> getRevenueByProductSubSub() {
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();
        
        // Maps pour SMB et EBT
        Map<String, BigDecimal> smbProducts = new LinkedHashMap<>();
        Map<String, BigDecimal> ebtProducts = new LinkedHashMap<>();
        Map<String, BigDecimal> globalProducts = new LinkedHashMap<>();
        
        BigDecimal smbTotal = BigDecimal.ZERO;
        BigDecimal ebtTotal = BigDecimal.ZERO;
        BigDecimal globalTotal = BigDecimal.ZERO;
        
        // Parcourir toutes les données préparées
        for (PreparedData salesData : preparedDataRepository.findAll()) {
            if (salesData.getRevenue() == null || 
                salesData.getCustomerType() == null || 
                salesData.getProdSubdinarySubdinary() == null) {
                continue;
            }
            
            String productSubSub = salesData.getProdSubdinarySubdinary().trim();
            BigDecimal revenue = salesData.getRevenue();
            String customerType = salesData.getCustomerType().trim();
            
            // Ajouter au global
            globalProducts.put(productSubSub, 
                globalProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
            globalTotal = globalTotal.add(revenue);
            
            // Ajouter selon le type de client
            if ("SMB".equalsIgnoreCase(customerType)) {
                smbProducts.put(productSubSub, 
                    smbProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
                smbTotal = smbTotal.add(revenue);
            } else if ("EBT".equalsIgnoreCase(customerType)) {
                ebtProducts.put(productSubSub, 
                    ebtProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
                ebtTotal = ebtTotal.add(revenue);
            }
        }
        
        // Ajouter les totaux
        smbProducts.put("total", smbTotal);
        ebtProducts.put("total", ebtTotal);
        globalProducts.put("total", globalTotal);
        
        // Construire le résultat
        result.put("SMB", smbProducts);
        result.put("EBT", ebtProducts);
        result.put("global", globalProducts);
        
        return result;
    }

    // ========== ARCHIVE METHODS (filtered by year/quarter/week) ==========

    public List<Map<String, Object>> getRevenueWithTargetsDetailedArchive(Integer year, String quarter, Integer week) {
        List<TopReseller> topResellers = topResellerRepository.findAll();
        Map<String, Map<String, Map<String, BigDecimal>>> detailByChannel = new HashMap<>();

        // Initialisation structure channel/type/segment
        for (TopReseller reseller : topResellers) {
            detailByChannel.put(reseller.getName(), new HashMap<>());
            detailByChannel.get(reseller.getName()).put("knoxSW", new HashMap<>());
            detailByChannel.get(reseller.getName()).put("service", new HashMap<>());
            detailByChannel.get(reseller.getName()).get("knoxSW").put("smb", BigDecimal.ZERO);
            detailByChannel.get(reseller.getName()).get("knoxSW").put("ebt", BigDecimal.ZERO);
            detailByChannel.get(reseller.getName()).get("service").put("smb", BigDecimal.ZERO);
            detailByChannel.get(reseller.getName()).get("service").put("ebt", BigDecimal.ZERO);
        }

        // Agrégation depuis archive
        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            String channel = salesData.getChannel();
            String productType = salesData.getProductType();
            String customerType = salesData.getCustomerType();
            BigDecimal revenue = salesData.getRevenue();

            if (channel == null || productType == null || customerType == null) continue;
            if (!detailByChannel.containsKey(channel)) continue;

            String prodKey = productType.equalsIgnoreCase("Knox SW") ? "knoxSW" :
                             productType.equalsIgnoreCase("Service") ? "service" : null;
            String custKey = customerType.equalsIgnoreCase("SMB") ? "smb" :
                             customerType.equalsIgnoreCase("EBT") ? "ebt" : null;

            if (prodKey != null && custKey != null) {
                detailByChannel.get(channel).get(prodKey).merge(custKey, revenue, BigDecimal::add);
            }
        }

        // Construction du résultat avec calcul du total revenue
        List<Map<String, Object>> result = new ArrayList<>();
        for (TopReseller reseller : topResellers) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("channel", reseller.getName());
            item.put("knoxSW", detailByChannel.get(reseller.getName()).get("knoxSW"));
            item.put("service", detailByChannel.get(reseller.getName()).get("service"));
            item.put("target", reseller.getTarget());
            
            // Calculer le total revenue
            BigDecimal totalRevenue = detailByChannel.get(reseller.getName())
                    .values().stream()
                    .flatMap(m -> m.values().stream())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.put("revenue", totalRevenue);
            
            result.add(item);
        }
        
        // ✅ Trier par revenue décroissant
        result.sort((a, b) -> {
            BigDecimal revA = (BigDecimal) a.get("revenue");
            BigDecimal revB = (BigDecimal) b.get("revenue");
            return revB.compareTo(revA);
        });

        return result;
    }

    public Map<String, BigDecimal> getCaEbtArchive(Integer year, String quarter, Integer week) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal service = BigDecimal.ZERO;
        BigDecimal knoxSw = BigDecimal.ZERO;

        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            if ("EBT".equalsIgnoreCase(salesData.getCustomerType())) {
                BigDecimal revenue = salesData.getRevenue();
                total = total.add(revenue);

                String productType = salesData.getProductType();
                if ("Service".equalsIgnoreCase(productType)) {
                    service = service.add(revenue);
                } else if ("Knox SW".equalsIgnoreCase(productType)) {
                    knoxSw = knoxSw.add(revenue);
                }
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("total", total);
        result.put("service", service);
        result.put("knoxSw", knoxSw);
        return result;
    }

    public Map<String, BigDecimal> getCaSmbArchive(Integer year, String quarter, Integer week) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal service = BigDecimal.ZERO;
        BigDecimal knoxSw = BigDecimal.ZERO;

        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            if ("SMB".equalsIgnoreCase(salesData.getCustomerType())) {
                BigDecimal revenue = salesData.getRevenue();
                total = total.add(revenue);

                String productType = salesData.getProductType();
                if ("Service".equalsIgnoreCase(productType)) {
                    service = service.add(revenue);
                } else if ("Knox SW".equalsIgnoreCase(productType)) {
                    knoxSw = knoxSw.add(revenue);
                }
            }
        }

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("total", total);
        result.put("service", service);
        result.put("knoxSw", knoxSw);
        return result;
    }

    public Map<Integer, Map<String, Map<String, BigDecimal>>> getGlobalRevenueByYearArchive(Integer year, String quarter, Integer week) {
        Map<Integer, Map<String, Map<String, BigDecimal>>> result = new HashMap<>();

        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            BigDecimal yearDecimal = salesData.getYear();
            int yearValue = yearDecimal.intValue();

            String month = salesData.getMonth();
            BigDecimal revenue = salesData.getRevenue();
            String customerType = salesData.getCustomerType();

            // Initialiser les maps pour l'année si elles n'existent pas
            result.putIfAbsent(yearValue, new HashMap<>());
            Map<String, Map<String, BigDecimal>> yearData = result.get(yearValue);

            yearData.putIfAbsent("Global SMB", new HashMap<>());
            yearData.putIfAbsent("Global EBT", new HashMap<>());
            yearData.putIfAbsent("Global Revenue", new HashMap<>());

            if ("SMB".equals(customerType)) {
                yearData.get("Global SMB").merge(month, revenue, BigDecimal::add);
            } else {
                yearData.get("Global EBT").merge(month, revenue, BigDecimal::add);
            }

            yearData.get("Global Revenue").merge(month, revenue, BigDecimal::add);
        }

        return result;
    }

    // ========== ARCHIVE METHOD FOR SMB DISTRIBUTION ==========
    public Map<String, Map<String, Object>> getSmbDistributionByResellerTypNameArchive(Integer year, String quarter, Integer week) {
        Map<String, BigDecimal> totalRevenueByReseller = new LinkedHashMap<>();
        Map<String, BigDecimal> totalKnoxSWByReseller = new LinkedHashMap<>();
        Map<String, BigDecimal> totalServiceByReseller = new LinkedHashMap<>();

        // Étape 1 : collecte des données depuis archive
        for (com.demo.Model.Channel.SalesDataArchive salesData : salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week)) {
            if (salesData.getCustomerType() != null &&
                    "SMB".equalsIgnoreCase(salesData.getCustomerType().trim()) &&
                    salesData.getResellerTypeName() != null &&
                    salesData.getRevenue() != null &&
                    salesData.getProductType() != null) {

                String resellerType = salesData.getResellerTypeName();
                BigDecimal revenue = salesData.getRevenue();
                String productType = salesData.getProductType().trim();

                // total par type de revendeur
                totalRevenueByReseller.put(
                        resellerType,
                        totalRevenueByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                );

                // par produit : Knox SW / Service
                if ("Knox SW".equalsIgnoreCase(productType)) {
                    totalKnoxSWByReseller.put(
                            resellerType,
                            totalKnoxSWByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                } else if ("Service".equalsIgnoreCase(productType)) {
                    totalServiceByReseller.put(
                            resellerType,
                            totalServiceByReseller.getOrDefault(resellerType, BigDecimal.ZERO).add(revenue)
                    );
                }
            }
        }

        // Étape 2 : somme des 3 revenus retenus pour le pourcentage
        BigDecimal localTotalRevenue = totalRevenueByReseller.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (localTotalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyMap();
        }

        // Étape 3 : création du résultat avec tri décroissant
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        
        // Créer une liste temporaire pour le tri
        List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<>(totalRevenueByReseller.entrySet());
        
        // ✅ Trier par revenue décroissant
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (Map.Entry<String, BigDecimal> entry : sortedEntries) {
            String reseller = entry.getKey();
            Map<String, Object> detailReseller = new LinkedHashMap<>();
            detailReseller.put("revenueByProductType", new LinkedHashMap<String, BigDecimal>() {{
                put("Knox SW", totalKnoxSWByReseller.getOrDefault(reseller, BigDecimal.ZERO));
                put("Service", totalServiceByReseller.getOrDefault(reseller, BigDecimal.ZERO));
            }});
            detailReseller.put("totalRevenue", totalRevenueByReseller.get(reseller));
            BigDecimal percentage = totalRevenueByReseller.get(reseller)
                    .divide(localTotalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            detailReseller.put("percentage", percentage);
            result.put(reseller, detailReseller);
        }
        return result;
    }

    public Map<String, List<PreparedData>> getTopDealsByCustomerTypeArchive(Integer year, String quarter, Integer week) {
        List<SalesDataArchive> allDeals = salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week);
        
        // Trier tous les deals par revenu décroissant
        allDeals.sort((d1, d2) -> d2.getRevenue().compareTo(d1.getRevenue()));
        
        // Filtrer et limiter à 5 par type de client (inclure même les N/A)
        List<PreparedData> topSMB = allDeals.stream()
                .filter(d -> "SMB".equalsIgnoreCase(d.getCustomerType()))
                .limit(5)
                .map(d -> convertToOldModel(d))  // Convert from archive to PreparedData
                .collect(Collectors.toList());
        
        List<PreparedData> topEBT = allDeals.stream()
                .filter(d -> "EBT".equalsIgnoreCase(d.getCustomerType()))
                .limit(5)
                .map(d -> convertToOldModel(d))
                .collect(Collectors.toList());
        
        Map<String, List<PreparedData>> result = new HashMap<>();
        result.put("SMB", topSMB);
        result.put("EBT", topEBT);
        
        return result;
    }

    public Map<String, Map<String, BigDecimal>> getRevenueByProductSubSubArchive(Integer year, String quarter, Integer week) {
        Map<String, Map<String, BigDecimal>> result = new LinkedHashMap<>();
        
        // Maps pour SMB et EBT
        Map<String, BigDecimal> smbProducts = new LinkedHashMap<>();
        Map<String, BigDecimal> ebtProducts = new LinkedHashMap<>();
        Map<String, BigDecimal> globalProducts = new LinkedHashMap<>();
        
        BigDecimal smbTotal = BigDecimal.ZERO;
        BigDecimal ebtTotal = BigDecimal.ZERO;
        BigDecimal globalTotal = BigDecimal.ZERO;
        
        // Parcourir les données archivées filtrées
        List<SalesDataArchive> archiveData = salesDataArchiveRepository.findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week);
        for (SalesDataArchive salesData : archiveData) {
            if (salesData.getRevenue() == null || 
                salesData.getCustomerType() == null || 
                salesData.getProdSubdinarySubdinary() == null) {
                continue;
            }
            
            String productSubSub = salesData.getProdSubdinarySubdinary().trim();
            BigDecimal revenue = salesData.getRevenue();
            String customerType = salesData.getCustomerType().trim();
            
            // Ajouter au global
            globalProducts.put(productSubSub, 
                globalProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
            globalTotal = globalTotal.add(revenue);
            
            // Ajouter selon le type de client
            if ("SMB".equalsIgnoreCase(customerType)) {
                smbProducts.put(productSubSub, 
                    smbProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
                smbTotal = smbTotal.add(revenue);
            } else if ("EBT".equalsIgnoreCase(customerType)) {
                ebtProducts.put(productSubSub, 
                    ebtProducts.getOrDefault(productSubSub, BigDecimal.ZERO).add(revenue));
                ebtTotal = ebtTotal.add(revenue);
            }
        }
        
        // Ajouter les totaux
        smbProducts.put("total", smbTotal);
        ebtProducts.put("total", ebtTotal);
        globalProducts.put("total", globalTotal);
        
        // Construire le résultat
        result.put("SMB", smbProducts);
        result.put("EBT", ebtProducts);
        result.put("global", globalProducts);
        
        return result;
    }

    private PreparedData convertToOldModel(SalesDataArchive archive) {
        PreparedData prepared = new PreparedData();
        
        // Copy all original PreparedData fields
        prepared.setId(null);  // Generate new ID for converted object
        prepared.setReseller(archive.getReseller());
        prepared.setResellerType(archive.getResellerType());
        prepared.setSecondReseller(archive.getSecondReseller());
        prepared.setRegion(archive.getRegion());
        prepared.setSubsidiary(archive.getSubsidiary());
        prepared.setEndCustomer(archive.getEndCustomer());
        prepared.setEndCustomerIndustry(archive.getEndCustomerIndustry());
        prepared.setProdSubdinary(archive.getProdSubdinary());
        prepared.setProdSubdinarySubdinary(archive.getProdSubdinarySubdinary());
        prepared.setLicense(archive.getLicense());
        prepared.setYear(archive.getYear());
        prepared.setMonth(archive.getMonth());
        prepared.setRevenue(archive.getRevenue());
        prepared.setLicenceQuantity(archive.getLicenceQuantity());
        prepared.setDiscountRate(archive.getDiscountRate());
        prepared.setBeforeDiscount(archive.getBeforeDiscount());
        
        // Copy enriched columns
        prepared.setResellerTypeName(archive.getResellerTypeName());
        prepared.setChannel(archive.getChannel());
        prepared.setCustomerType(archive.getCustomerType());
        prepared.setProductType(archive.getProductType());
        
        return prepared;
    }
}

