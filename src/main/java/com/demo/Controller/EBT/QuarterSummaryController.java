package com.demo.Controller.EBT;

import com.demo.Model.EBT.QuarterSummary;
import com.demo.service.EBT.QuarterSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import java.util.Map;

@CrossOrigin(
    origins = "http://localhost",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/ebt/quarter-summary")
public class QuarterSummaryController {

    @Autowired
    private QuarterSummaryService quarterSummaryService;

    // GET by quarter
    @GetMapping("/get")
    public ResponseEntity<QuarterSummary> getByQuarter(@RequestParam String quarter) {
        try {
            QuarterSummary data = quarterSummaryService.getByQuarter(quarter);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // UPDATE target amount
    @PutMapping("/update-target")
    public ResponseEntity<?> updateTargetAmount(
            @RequestParam String quarter,
            @RequestBody Map<String, Double> payload) {
        try {
            Double targetAmount = payload.get("targetAmount");
            QuarterSummary updated = quarterSummaryService.updateTargetAmount(quarter, targetAmount);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }
     @GetMapping("/get-all")
    public ResponseEntity<List<QuarterSummary>> getAllQuarters() {
        try {
            List<QuarterSummary> allData = quarterSummaryService.getAllQuarters();
            return ResponseEntity.ok(allData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
