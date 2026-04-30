package com.demo.Controller.Channel;

import com.demo.service.Channel.ArchiveEvolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/archive")
public class ArchiveController {
    
    @Autowired
    private ArchiveEvolutionService evolutionService;
    
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
