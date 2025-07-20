package com.demo.Controller;


import com.demo.DTO.ChannelRevenueDTO;
import com.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, BigDecimal>> getSmbDistribution() {
        Map<String, BigDecimal> distribution = dashboardService.getSmbDistributionByResellerTypName();
        return ResponseEntity.ok(distribution);
    }
    @GetMapping("/ebt")
    public ResponseEntity<  Map<String, BigDecimal> > getEbt() {
        return ResponseEntity.ok(dashboardService.getCaEbt());
    }
    @GetMapping("/smb")
    public ResponseEntity<  Map<String, BigDecimal> > getSmb() {
        return ResponseEntity.ok(dashboardService.getCaSmb());
    }


}
