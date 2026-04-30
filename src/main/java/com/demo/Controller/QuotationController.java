package com.demo.Controller;

import com.demo.Model.Quotation;
import com.demo.dto.QuotationDTO;
import com.demo.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {
    
    private static final Logger logger = Logger.getLogger(QuotationController.class.getName());
    
    @Autowired
    private QuotationService quotationService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Quotation> quotations = quotationService.getAllQuotations(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", quotations.getContent());
            response.put("totalElements", quotations.getTotalElements());
            response.put("totalPages", quotations.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération des quotations: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuotationById(@PathVariable Long id) {
        try {
            Quotation quotation = quotationService.getQuotationById(id);
            return ResponseEntity.ok(quotation);
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération de la quotation: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createQuotation(@RequestBody QuotationDTO dto) {
        try {
            Quotation quotation = quotationService.createQuotation(dto);
            return ResponseEntity.ok(quotation);
        } catch (Exception e) {
            logger.severe("Erreur lors de la création de la quotation: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuotation(@PathVariable Long id, @RequestBody QuotationDTO dto) {
        try {
            Quotation quotation = quotationService.updateQuotation(id, dto);
            return ResponseEntity.ok(quotation);
        } catch (Exception e) {
            logger.severe("Erreur lors de la mise à jour de la quotation: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuotation(@PathVariable Long id) {
        try {
            quotationService.deleteQuotation(id);
            return ResponseEntity.ok(Map.of("message", "Quotation supprimée avec succès"));
        } catch (Exception e) {
            logger.severe("Erreur lors de la suppression de la quotation: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadQuotations(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Fichier vide"));
            }
            
            List<Quotation> quotations = quotationService.uploadQuotationsFromExcel(file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quotations importées avec succès");
            response.put("count", quotations.size());
            response.put("data", quotations);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.severe("Erreur lors du téléchargement du fichier: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Erreur du fichier: " + e.getMessage()));
        } catch (Exception e) {
            logger.severe("Erreur lors de l'import des quotations: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
