package com.demo.service.Channel;

import com.demo.DTO.Channel.ChannelRevenueDTO;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Model.Channel.TopReseller;
import com.demo.Repository.Channel.*;
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

    @Autowired
    private TopResellerRepository topResellerRepository;

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

    // Construction de la liste, insertion dans l'ordre désiré
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
            channels.add(elmt.getChannel());
        }
        return channels;
    }

    public void createTopReseller(TopReseller topreseller){
        topResellerRepository.save(topreseller);
    }



}
