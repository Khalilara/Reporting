package com.demo.Controller.Channel;


import com.demo.DTO.Channel.ChannelRevenueDTO;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.TopReseller;
import com.demo.service.Channel.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/dashboard")
public class DasboardController {
    @Autowired
    DashboardService dashboardService;


 @GetMapping("/channel")
public ResponseEntity<List<Map<String, Object>>> channel() {
    List<Map<String, Object>> data = dashboardService.getRevenueWithTargetsDetailed();
    return ResponseEntity.ok(data);
}

 @GetMapping("/channel/archive")
public ResponseEntity<List<Map<String, Object>>> channelArchive(
        @RequestParam Integer year,
        @RequestParam String quarter,
        @RequestParam Integer week) {
    List<Map<String, Object>> data = dashboardService.getRevenueWithTargetsDetailedArchive(year, quarter, week);
    return ResponseEntity.ok(data);
}


    @GetMapping("/smb-distribution")
    public ResponseEntity<Map<String, Map<String, Object>>> getSmbDistribution() {
        Map<String, Map<String, Object>> distribution = dashboardService.getSmbDistributionByResellerTypName();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/smb-distribution/archive")
    public ResponseEntity<Map<String, Map<String, Object>>> getSmbDistributionArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        Map<String, Map<String, Object>> distribution = dashboardService.getSmbDistributionByResellerTypNameArchive(year, quarter, week);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/ebt")
    public ResponseEntity<Map<String, BigDecimal>> getEbt() {
        return ResponseEntity.ok(dashboardService.getCaEbt());
    }

    @GetMapping("/ebt/archive")
    public ResponseEntity<Map<String, BigDecimal>> getEbtArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        return ResponseEntity.ok(dashboardService.getCaEbtArchive(year, quarter, week));
    }

    @GetMapping("/smb")
    public ResponseEntity<Map<String, BigDecimal>> getSmb() {
        return ResponseEntity.ok(dashboardService.getCaSmb());
    }

    @GetMapping("/smb/archive")
    public ResponseEntity<Map<String, BigDecimal>> getSmbArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        return ResponseEntity.ok(dashboardService.getCaSmbArchive(year, quarter, week));
    }

    @GetMapping("/top-deals")
    public ResponseEntity<Map<String, List<PreparedData>>> getTopDeals() {
        return ResponseEntity.ok(dashboardService.getTopDealsByCustomerType());
    }

    @GetMapping("/top-deals/archive")
    public ResponseEntity<Map<String, List<PreparedData>>> getTopDealsArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        return ResponseEntity.ok(dashboardService.getTopDealsByCustomerTypeArchive(year, quarter, week));
    }

    @GetMapping("/global")
    public ResponseEntity<Map<Integer, Map<String, Map<String, BigDecimal>>>> getGlobalRevenue() {
        return ResponseEntity.ok(dashboardService.getGlobalRevenueByYear());
    }

    @GetMapping("/global/archive")
    public ResponseEntity<Map<Integer, Map<String, Map<String, BigDecimal>>>> getGlobalRevenueArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        return ResponseEntity.ok(dashboardService.getGlobalRevenueByYearArchive(year, quarter, week));
    }

    @PostMapping("/create-reseller")
    public ResponseEntity<?> createReseller(@RequestBody TopReseller topReseller) {
        List<String> channels = dashboardService.getChannelResellers();

        if (!channels.contains(topReseller.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur : ce canal n'existe pas !");
        }

        dashboardService.createTopReseller(topReseller);
        return ResponseEntity.ok("Canal créé avec succès");
    }
    @GetMapping("/reseller-names")
    public ResponseEntity<List<String>> getResellerNames() {
        List<String> channels = dashboardService.getChannelResellers();

        // Vérification null safe
        if (channels == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Filtrer les null, vides, et supprimer les doublons
        List<String> filteredChannels = channels.stream()
                .filter(Objects::nonNull) // Enlever les null
                .map(String::trim) // Nettoyer les espaces
                .filter(channel -> !channel.isEmpty()) // Enlever les chaînes vides
                .distinct() // Enlever les doublons
                .sorted() // Trier alphabétiquement
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredChannels);
    }
    @PutMapping("/modifier-target")
    public ResponseEntity<?> modifierTarget(@RequestParam String topResellerName, @RequestParam BigDecimal target) {
        try {
            dashboardService.updateTopResellerTarget(topResellerName, target);
            return ResponseEntity.ok("Target Updated Successfully");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }
    @GetMapping("/revenue-by-product-subsub")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getRevenueByProductSubSub() {
        return ResponseEntity.ok(dashboardService.getRevenueByProductSubSub());
    }

    @GetMapping("/revenue-by-product-subsub/archive")
    public ResponseEntity<Map<String, Map<String, BigDecimal>>> getRevenueByProductSubSubArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        return ResponseEntity.ok(dashboardService.getRevenueByProductSubSubArchive(year, quarter, week));
    }

}
