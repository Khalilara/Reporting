package com.demo.Controller;

import com.demo.Model.Pipe;
import com.demo.dto.PipeDTO;
import com.demo.service.PipeService;
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
@RequestMapping("/api/pipes")
public class PipeController {
    
    private static final Logger logger = Logger.getLogger(PipeController.class.getName());
    
    @Autowired
    private PipeService pipeService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Pipe> pipes = pipeService.getAllPipes(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", pipes.getContent());
            response.put("totalElements", pipes.getTotalElements());
            response.put("totalPages", pipes.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération des pipes: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPipeById(@PathVariable Long id) {
        try {
            Pipe pipe = pipeService.getPipeById(id);
            return ResponseEntity.ok(pipe);
        } catch (Exception e) {
            logger.severe("Erreur lors de la récupération du pipe: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createPipe(@RequestBody PipeDTO dto) {
        try {
            Pipe pipe = pipeService.createPipe(dto);
            return ResponseEntity.ok(pipe);
        } catch (Exception e) {
            logger.severe("Erreur lors de la création du pipe: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePipe(@PathVariable Long id, @RequestBody PipeDTO dto) {
        try {
            Pipe pipe = pipeService.updatePipe(id, dto);
            return ResponseEntity.ok(pipe);
        } catch (Exception e) {
            logger.severe("Erreur lors de la mise à jour du pipe: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePipe(@PathVariable Long id) {
        try {
            pipeService.deletePipe(id);
            return ResponseEntity.ok(Map.of("message", "Pipe supprimé avec succès"));
        } catch (Exception e) {
            logger.severe("Erreur lors de la suppression du pipe: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPipes(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Fichier vide"));
            }
            
            List<Pipe> pipes = pipeService.uploadPipesFromExcel(file);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Pipes importés avec succès");
            response.put("count", pipes.size());
            response.put("data", pipes);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.severe("Erreur lors du téléchargement du fichier: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Erreur du fichier: " + e.getMessage()));
        } catch (Exception e) {
            logger.severe("Erreur lors de l'import des pipes: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
