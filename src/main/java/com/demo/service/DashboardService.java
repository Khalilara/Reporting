package com.demo.service;

import com.demo.DTO.ChannelRevenueDTO;
import com.demo.Model.PreparedData;
import com.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

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
    private static final Map<String, BigDecimal> quarterlyTargets = Map.ofEntries(
            Map.entry("Econocom", new BigDecimal("50000")),
            Map.entry("Inmac", new BigDecimal("40000")),
            Map.entry("HELIAQ", new BigDecimal("10000")),
            Map.entry("Computacenter", new BigDecimal("60000")),
            Map.entry("SFR", new BigDecimal("30000")),
            Map.entry("Orange", new BigDecimal("80000")),
            Map.entry("BOUYGUES", new BigDecimal("25000")),
            Map.entry("ILCO", new BigDecimal("15000")),
            Map.entry("KOESIO", new BigDecimal("20000")),
            Map.entry("SCC", new BigDecimal("70000")),
            Map.entry("LAFI", new BigDecimal("35000")),
            Map.entry("CFI", new BigDecimal("20000")),
            Map.entry("Bechtle", new BigDecimal("30000")),
            Map.entry("Saphelec", new BigDecimal("18000")),
            Map.entry("ByLink", new BigDecimal("16000"))
    );
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

    public List<ChannelRevenueDTO> getRevenueWithTargets() {
        Map<String, BigDecimal> revenueByChannel = new HashMap<>();

        List<String> allowedChannels = new ArrayList<>(quarterlyTargets.keySet());

        for (PreparedData salesData : preparedDataRepository.findAll()) {
            String channel = salesData.getChannel();
            BigDecimal revenue = salesData.getRevenue();

            if (channel != null && allowedChannels.contains(channel)) {
                revenueByChannel.put(
                        channel,
                        revenueByChannel.getOrDefault(channel, BigDecimal.ZERO).add(revenue)
                );
            }
        }

        List<ChannelRevenueDTO> result = new ArrayList<>();
        for (String channel : revenueByChannel.keySet()) {
            result.add(new ChannelRevenueDTO(
                    channel,
                    revenueByChannel.get(channel),
                    quarterlyTargets.get(channel)
            ));
        }

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

        for (String resellerType : totalRevenueByReseller.keySet()) {
            BigDecimal resellerRevenue = totalRevenueByReseller.get(resellerType);

            BigDecimal percentage = resellerRevenue
                    .multiply(BigDecimal.valueOf(100))
                    .divide(localTotalRevenue, 2, RoundingMode.HALF_UP);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRevenue", resellerRevenue);
            stats.put("percentage", percentage);

            Map<String, BigDecimal> productBreakdown = new HashMap<>();
            productBreakdown.put("Knox SW", totalKnoxSWByReseller.getOrDefault(resellerType, BigDecimal.ZERO));
            productBreakdown.put("Service", totalServiceByReseller.getOrDefault(resellerType, BigDecimal.ZERO));

            stats.put("revenueByProductType", productBreakdown);

            result.put(resellerType, stats);
        }

        return result;
    }

    public List<PreparedData> getTopDeals() {
        List<PreparedData> allDeals = preparedDataRepository.findAll();

        // Trier les deals par revenu décroissant (BigDecimal)
        allDeals.sort((d1, d2) -> d2.getRevenue().compareTo(d1.getRevenue()));

        // Retourner les 5 premiers
        return allDeals.stream().limit(5).collect(Collectors.toList());
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
                yearData.get("Global SMB").merge(month, revenue, BigDecimal::add);
            } else {
                yearData.get("Global EBT").merge(month, revenue, BigDecimal::add);
            }

            yearData.get("Global Revenue").merge(month, revenue, BigDecimal::add);
        }

        return result;
    }


}
