package com.demo.Controller.Channel;


import com.demo.DTO.Channel.ChannelRevenueDTO;
import com.demo.Model.Channel.PreparedData;
import com.demo.service.Channel.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/dashboard")
public class DasboardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/channel")
    public ResponseEntity<List<ChannelRevenueDTO>> channel() {
        List<ChannelRevenueDTO> data = dashboardService.getRevenueWithTargets();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/smb-distribution")
    public ResponseEntity<Map<String, Map<String, Object>>> getSmbDistribution() {
        Map<String, Map<String, Object>> distribution = dashboardService.getSmbDistributionByResellerTypName();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/ebt")
    public ResponseEntity<Map<String, BigDecimal>> getEbt() {
        return ResponseEntity.ok(dashboardService.getCaEbt());
    }

    @GetMapping("/smb")
    public ResponseEntity<Map<String, BigDecimal>> getSmb() {
        return ResponseEntity.ok(dashboardService.getCaSmb());
    }

    @GetMapping("/top-deals")
    public ResponseEntity<List<PreparedData>> getTopDeals() {
        return ResponseEntity.ok(dashboardService.getTopDeals());
    }
    @GetMapping("/global")
    public ResponseEntity<Map<Integer, Map<String, Map<String, BigDecimal>>>> getGlobalRevenue() {
        return ResponseEntity.ok(dashboardService.getGlobalRevenueByYear());
    }


}
