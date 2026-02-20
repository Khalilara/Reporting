package com.demo.controller.Channel;

import com.demo.Model.Channel.WeeklyEvolution;
import com.demo.service.Channel.WeeklyEvolutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(
    origins = "http://localhost",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/weekly-evolution")
public class WeeklyEvolutionController {

    @Autowired
    private WeeklyEvolutionService evolutionService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Crée un nouveau snapshot manuel
     * Exemple : POST /api/weekly-evolution/create?date=2026-02-08&weekLabel=W12
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSnapshot(
            @RequestParam String date, 
            @RequestParam String weekLabel) {
        try {
            String cleanDate = date.trim();
            LocalDate localDate = LocalDate.parse(cleanDate, FORMATTER);
            
            // ✅ CORRECTION : passage du weekLabel au service
            WeeklyEvolution snapshot = evolutionService.createManualSnapshot(localDate, weekLabel);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Snapshot créé avec succès");
            response.put("data", snapshot);
            
            return ResponseEntity.ok(response);
            
        } catch (DateTimeParseException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Format de date invalide. Utilisez YYYY-MM-DD (ex: 2026-02-08)");
            error.put("received", date);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupère tout l'historique
     * Exemple : GET /api/weekly-evolution/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<WeeklyEvolution>> getHistory() {
        List<WeeklyEvolution> history = evolutionService.getHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Récupère l'historique filtré par année
     * Exemple : GET /api/weekly-evolution/history/2026
     */
    @GetMapping("/history/{year}")
    public ResponseEntity<List<WeeklyEvolution>> getHistoryByYear(@PathVariable Integer year) {
        List<WeeklyEvolution> history = evolutionService.getHistoryByYear(year);
        return ResponseEntity.ok(history);
    }

    /**
     * Récupère le dernier snapshot
     * Exemple : GET /api/weekly-evolution/latest
     */
    @GetMapping("/latest")
    public ResponseEntity<WeeklyEvolution> getLatest() {
        Optional<WeeklyEvolution> latest = evolutionService.getLatestSnapshot();
        if (latest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latest.get());
    }

    /**
     * Compare deux semaines
     * Exemple : GET /api/weekly-evolution/compare?week1=2026-02-01&week2=2026-02-08
     */
    @GetMapping("/compare")
    public ResponseEntity<?> compareWeeks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate week1,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate week2) {
        try {
            Map<String, Object> comparison = evolutionService.compareWeeks(week1, week2);
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Supprime un snapshot par son ID
     * Exemple : DELETE /api/weekly-evolution/delete/1
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSnapshot(@PathVariable Long id) {
        try {
            evolutionService.deleteSnapshot(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Snapshot supprimé avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}