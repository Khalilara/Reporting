package com.demo.Controller.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.*;
import com.demo.service.Channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(
        origins = "http://106.102.1.60",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {

    @Autowired
    private SalesDataService service;

    @Autowired
    private ArchiveDataService archiveService;

    @Autowired
    private ExcelServiceReader excelReader;

    @Autowired
    private PreparedDataRepository preparedDataRepository;

    @Autowired
    private SalesDataRepository salesDataRepository;

    @Autowired
    private ResellerCategRepository resellerCategRepository;

    @Autowired
    private CustomerCategRepository customerCategRepository;

    @Autowired
    private ProductCategRepository productCategRepository;

    @Autowired
    private ResellerWithOut2ndResellerRepository resellerWithOut2ndResellerRepository;

    @Autowired
    private DataPreparationService dataserviceprepared;

    @GetMapping("/Hello")
    public ResponseEntity<String> Hello() {
        return ResponseEntity.ok("Hello depuis le contrôleur !");
    }

    @GetMapping("/sales-data")
    public ResponseEntity<List<SalesData>> getSalesData() {
        return ResponseEntity.ok(salesDataRepository.findAll());
    }

    @GetMapping("/prepared-data")
    public ResponseEntity<List<PreparedData>> getPreparedData() {
        return ResponseEntity.ok(preparedDataRepository.findAll());
    }

    @GetMapping("/customer-categories")
    public ResponseEntity<List<CustumerCateg>> getCustomerCategories() {
        return ResponseEntity.ok(customerCategRepository.findAll());
    }

    @GetMapping("/reseller-categories")
    public ResponseEntity<List<ResellerCateg>> getResellerCategories() {
        return ResponseEntity.ok(resellerCategRepository.findAll());
    }

    @GetMapping("/product-categories")
    public ResponseEntity<List<ProductCateg>> getProductCategories() {
        return ResponseEntity.ok(productCategRepository.findAll());
    }

    @GetMapping("/reseller-without-2nd-reseller")
    public ResponseEntity<List<ResellerWithOut2ndReseller>> getResellerWithOut2ndReseller() {
        return ResponseEntity.ok(resellerWithOut2ndResellerRepository.findAll());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<SalesData> dataList = excelReader.readExcelFile(file.getInputStream());

            new Thread(() -> {
                try {
                    System.out.println("Début import sales_data en arrière-plan...");
                    service.saveAll(dataList);
                    System.out.println("Import sales_data terminé.");
                } catch (Exception e) {
                    System.out.println("Erreur import sales_data async : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            return ResponseEntity.ok("Fichier reçu. Import lancé en arrière-plan.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/upload/customer")
    public ResponseEntity<String> uploadExcel2(@RequestParam("file") MultipartFile file) {
        try {
            List<CustumerCateg> dataList = excelReader.readExcelFile2(file.getInputStream());

            new Thread(() -> {
                try {
                    System.out.println("Début import customer en arrière-plan...");
                    service.saveAll2(dataList);
                    dataserviceprepared.prepareData();
                    System.out.println("Import customer terminé.");
                } catch (Exception e) {
                    System.out.println("Erreur import customer async : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            return ResponseEntity.ok("Fichier reçu. Import lancé en arrière-plan.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/upload/reseller")
    public ResponseEntity<String> uploadExcelReseller(@RequestParam("file") MultipartFile file) {
        try {
            List<ResellerCateg> dataList = excelReader.readExcelFileReseller(file.getInputStream());

            new Thread(() -> {
                try {
                    System.out.println("Début import reseller en arrière-plan...");
                    service.saveAllReseller(dataList);
                    dataserviceprepared.prepareData();
                    System.out.println("Import reseller terminé.");
                } catch (Exception e) {
                    System.out.println("Erreur import reseller async : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            return ResponseEntity.ok("Fichier reçu. Import lancé en arrière-plan.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/upload/product")
    public ResponseEntity<String> uploadExcelProduct(@RequestParam("file") MultipartFile file) {
        try {
            List<ProductCateg> dataList = excelReader.readExcelFileProduct(file.getInputStream());

            new Thread(() -> {
                try {
                    System.out.println("Début import product en arrière-plan...");
                    service.saveAllProduct(dataList);
                    dataserviceprepared.prepareData();
                    System.out.println("Import product terminé.");
                } catch (Exception e) {
                    System.out.println("Erreur import product async : " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            return ResponseEntity.ok("Fichier reçu. Import lancé en arrière-plan.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }

    @PostMapping("/archive-prepared-data")
    public ResponseEntity<Map<String, Object>> archivePreparedData(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week,
            @RequestParam(required = false) String weekDate) {

        try {
            ArchiveDataService.ArchiveResult result = archiveService.archiveCurrentPreparedData(
                    year, quarter, week, weekDate
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result.message,
                    "batchId", result.batchId,
                    "archivedRows", result.archivedCount,
                    "isOverwrite", result.isOverwrite
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Archive failed: " + e.getMessage()
                    ));
        }
    }
}