package com.demo.service;

import com.demo.DTO.ChannelRevenueDTO;
import com.demo.Model.PreparedData;
import com.demo.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public Map<String, BigDecimal> getSmbDistributionByResellerTypName() {
        Map<String, BigDecimal> revenueByType = new HashMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (PreparedData salesData : preparedDataRepository.findAll()) {
            if (salesData.getCustomerType() != null &&
                    "SMB".equalsIgnoreCase(salesData.getCustomerType().trim()) &&
                    salesData.getResellerTypeName() != null &&
                    salesData.getRevenue() != null) {

                BigDecimal revenue = salesData.getRevenue();
                String typeName = salesData.getResellerTypeName();

                totalRevenue = totalRevenue.add(revenue);
                revenueByType.put(
                        typeName,
                        revenueByType.getOrDefault(typeName, BigDecimal.ZERO).add(revenue)
                );
            }
        }

        if (totalRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return Collections.emptyMap();
        }

        Map<String, BigDecimal> percentageByType = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : revenueByType.entrySet()) {
            BigDecimal percentage = entry.getValue()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalRevenue, 2, RoundingMode.HALF_UP);
            percentageByType.put(entry.getKey(), percentage);
        }

        return percentageByType;
    }





}
