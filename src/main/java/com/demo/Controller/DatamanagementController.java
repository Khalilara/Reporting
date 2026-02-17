package com.demo.Controller;

import com.demo.service.Channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/data-management")
public class DatamanagementController {

    @Autowired
    private DataManagementService dataManagementService;

    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        try {
            Map<String, Long> deletedCounts = dataManagementService.clearAllData();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Toutes les données ont été supprimées avec succès");
            response.put("deletedCounts", deletedCounts);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur lors de la suppression: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}