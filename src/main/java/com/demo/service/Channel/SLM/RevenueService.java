package com.demo.service.Channel.SLM;

import com.demo.Repository.Channel.SLM.SlmRepository;
import com.demo.dto.RevenueInsight;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class RevenueService {

    private final SlmRepository slmRepository;

    public RevenueService(SlmRepository slmRepository) {
        this.slmRepository = slmRepository;
    }

    public List<RevenueInsight> getRevenueInsights() {
        List<String> clientTypes = Arrays.asList("SMB", "EBT", "");
        
        return clientTypes.stream().map(clientType -> {
            Double totalSLM = slmRepository.findTotalRevenueByClientType(clientType);
            
            // Simulation pour test (remplace par vraie jointure après)
            double commonRevenue = totalSLM != null ? totalSLM * 0.7 : 0;
            double onlySLM = (totalSLM != null ? totalSLM : 0) - commonRevenue;
            
            return new RevenueInsight(clientType, 
                totalSLM != null ? totalSLM : 0, 
                commonRevenue, 
                onlySLM);
        }).toList();
    }
}
