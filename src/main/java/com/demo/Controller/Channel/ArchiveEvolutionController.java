package com.demo.Controller.Channel;

import com.demo.service.Channel.ArchiveEvolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/archive/evolution")
public class ArchiveEvolutionController {
    
    @Autowired
    private ArchiveEvolutionService evolutionService;
    
    /**
     * Get revenue evolution by year
     * @param customerType: "all", "SMB", or "EBT" (default: "all")
     * @return Chart data with yearly revenue breakdown
     */
    @GetMapping("/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyEvolution(
            @RequestParam(defaultValue = "all") String customerType) {
        return ResponseEntity.ok(evolutionService.getYearlyEvolution(customerType));
    }
    
    /**
     * Get revenue evolution by semester
     * @param year: optional filter by specific year
     * @param customerType: "all", "SMB", or "EBT" (default: "all")
     * @return Chart data with semester revenue breakdown
     */
    @GetMapping("/semester")
    public ResponseEntity<Map<String, Object>> getSemesterEvolution(
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "all") String customerType) {
        return ResponseEntity.ok(evolutionService.getSemesterEvolution(year, customerType));
    }
    
    /**
     * Get revenue evolution by quarter
     * @param year: optional filter by specific year
     * @param customerType: "all", "SMB", or "EBT" (default: "all")
     * @return Chart data with quarterly revenue breakdown
     */
    @GetMapping("/quarterly")
    public ResponseEntity<Map<String, Object>> getQuarterlyEvolution(
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "all") String customerType) {
        return ResponseEntity.ok(evolutionService.getQuarterlyEvolution(year, customerType));
    }
    
    /**
     * Get revenue evolution by week
     * @param year: filter by specific year (required)
     * @param quarter: filter by specific quarter (required)
     * @param customerType: "all", "SMB", or "EBT" (default: "all")
     * @return Chart data with weekly revenue breakdown
     */
    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Object>> getWeeklyEvolution(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam(defaultValue = "all") String customerType) {
        return ResponseEntity.ok(evolutionService.getWeeklyEvolution(year, quarter, customerType));
    }
    
    /**
     * Get available years from archive for filters
     */
    @GetMapping("/available-years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        return ResponseEntity.ok(evolutionService.getAvailableYears());
    }
    
    /**
     * Get available quarters (always Q1-Q4)
     */
    @GetMapping("/available-quarters")
    public ResponseEntity<List<String>> getAvailableQuarters() {
        return ResponseEntity.ok(evolutionService.getAvailableQuarters());
    }
}
