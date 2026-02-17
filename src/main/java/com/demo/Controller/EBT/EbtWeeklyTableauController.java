package com.demo.Controller.EBT;

import com.demo.Model.EBT.EbtWeeklyTableau;
import com.demo.service.EBT.EbtWeeklyTableauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(
    origins = "http://localhost",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/ebt/weekly")
public class EbtWeeklyTableauController {

    @Autowired
    private EbtWeeklyTableauService ebtWeeklyTableauService;

    // GET all by quarter
    @GetMapping("/get_all")
    public ResponseEntity<List<EbtWeeklyTableau>> getByQuarter(@RequestParam String quarter) {
        try {
            List<EbtWeeklyTableau> data = ebtWeeklyTableauService.getAllByQuarter(quarter);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EbtWeeklyTableau ebtWeeklyTableau) {
        try {
            ebtWeeklyTableauService.create(ebtWeeklyTableau);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création: " + e.getMessage());
        }
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EbtWeeklyTableau ebtWeeklyTableau) {
        try {
            EbtWeeklyTableau updated = ebtWeeklyTableauService.update(id, ebtWeeklyTableau);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Élément non trouvé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la mise à jour: " + e.getMessage());
        }
    }

    // PATCH UPDATE (partial)
    @PatchMapping("/patch-update/{id}")
    public ResponseEntity<?> patchUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            EbtWeeklyTableau ebtWeeklyTableau = new EbtWeeklyTableau();
            
            if (updates.containsKey("weekNumber")) {
                ebtWeeklyTableau.setWeekNumber((String) updates.get("weekNumber"));
            }
            if (updates.containsKey("caWeekly")) {
                ebtWeeklyTableau.setCaWeekly(((Number) updates.get("caWeekly")).doubleValue());
            }
            if (updates.containsKey("comment")) {
                ebtWeeklyTableau.setComment((String) updates.get("comment"));
            }
            if (updates.containsKey("quarter")) {
                ebtWeeklyTableau.setQuarter((String) updates.get("quarter"));
            }

            EbtWeeklyTableau updated = ebtWeeklyTableauService.patchUpdate(id, ebtWeeklyTableau);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Élément non trouvé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la mise à jour partielle: " + e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            ebtWeeklyTableauService.delete(id);
            return ResponseEntity.ok("Élément supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la suppression: " + e.getMessage());
        }
    }
}
