package com.demo.Controller.Channel;


import com.demo.DTO.Channel.ChannelRevenueDTO;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.TopReseller;
import com.demo.service.Channel.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
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

}
