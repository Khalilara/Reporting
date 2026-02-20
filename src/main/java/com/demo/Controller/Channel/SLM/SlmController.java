package com.demo.Controller.Channel.SLM;

import com.demo.Model.Channel.SLM.SLM;
import com.demo.Model.Channel.SLM.EndCustomer;
import com.demo.Repository.Channel.SLM.SlmRepository;
import com.demo.Repository.Channel.SLM.EndCustomerRepository;
import com.demo.service.Channel.SLM.SlmService;
import com.demo.service.Channel.SLM.EndCustomerService;
import com.demo.service.Channel.SLM.SlmExportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import com.demo.dto.RevenueInsight;
import com.demo.service.Channel.SLM.RevenueService;
@CrossOrigin(origins = "http://localhost", allowCredentials = "true")
@RestController
@RequestMapping("/api/slm")
public class SlmController {

    @Autowired
    private SlmService slmService;

    @Autowired
    private SlmRepository slmRepository;

    @Autowired
    private EndCustomerService endCustomerService;

    @Autowired
    private EndCustomerRepository endCustomerRepository;

    @Autowired
    private SlmExportService slmExportService;
    
    @Autowired
    private RevenueService revenueService;

    /**
     * Récupère tous les enregistrements SLM
     * GET /api/slm/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<SLM>> getAllSLM() {
        try {
            List<SLM> slmList = slmRepository.findAll();
            return ResponseEntity.ok(slmList);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Endpoint pour importer le fichier Excel EndCustomer (2 colonnes)
     * POST /api/slm/import-end-customer
     */
    @PostMapping("/import-end-customer")
    public ResponseEntity<String> importEndCustomerExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide");
            }

            List<EndCustomer> endCustomerList = endCustomerService.importEndCustomerFromExcel(file.getInputStream());

            if (endCustomerList.isEmpty()) {
                return ResponseEntity.ok("Aucune donnée valide trouvée dans le fichier EndCustomer");
            }

            endCustomerRepository.saveAll(endCustomerList);

            return ResponseEntity.ok("Importation EndCustomer réussie : " + endCustomerList.size() + " enregistrements importés");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de la lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors du traitement : " + e.getMessage());
        }
    }

    /**
     * Endpoint pour importer le fichier Excel SLM
     * POST /api/slm/import
     */
    @PostMapping("/import")
    public ResponseEntity<String> importSLMExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide");
            }

            List<SLM> slmList = slmService.importSLMFromExcel(file.getInputStream());

            if (slmList.isEmpty()) {
                return ResponseEntity.ok("Aucune donnée valide trouvée (tous les Type étaient Trial ou erreur de jointure)");
            }

            slmRepository.saveAll(slmList);

            return ResponseEntity.ok("Importation SLM réussie : " + slmList.size() + " enregistrements importés");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de la lecture du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors du traitement : " + e.getMessage());
        }
    }

    /**
     * Supprime toutes les données SLM, EndCustomer et autres données liées
     * DELETE /api/slm/clear-all
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<String> clearAllData() {
        try {
            slmRepository.deleteAll();
            endCustomerRepository.deleteAll();
            return ResponseEntity.ok("Toutes les données SLM, EndCustomer ont été supprimées");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    /**
     * Export Excel : SLM avec clientType NULL ou vide (3 premières colonnes)
     * GET /api/slm/export-missing-client-type
     */
    @GetMapping("/export-missing-client-type")
    public ResponseEntity<byte[]> exportMissingClientType() throws Exception {
        byte[] fileContent = slmExportService.exportMissingClientTypeToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=slm_missing_client_type.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(fileContent);
    }


    @GetMapping("/revenue-insights")
    public ResponseEntity<List<RevenueInsight>> getRevenueInsights() {
        try {
            List<RevenueInsight> insights = revenueService.getRevenueInsights();
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
