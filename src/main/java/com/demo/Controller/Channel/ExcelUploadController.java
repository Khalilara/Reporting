package com.demo.Controller.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.PreparedDataRepository;
import com.demo.Repository.Channel.SalesDataRepository;
import com.demo.Repository.Channel.ResellerCategRepository;
import com.demo.Repository.Channel.CustomerCategRepository;
import com.demo.Repository.Channel.ProductCategRepository;
import com.demo.Repository.Channel.ResellerWithOut2ndResellerRepository;
import com.demo.service.Channel.SalesDataService;
import com.demo.service.Channel.DataPreparationService;
import com.demo.service.Channel.ExcelServiceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {
    @Autowired
    private SalesDataService service;

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

    // ========== ENDPOINTS DE TÉLÉCHARGEMENT (GET) ==========
    
    @GetMapping("/sales-data")
    public ResponseEntity<List<SalesData>> getSalesData() {
        List<SalesData> data = salesDataRepository.findAll();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/prepared-data")
    public ResponseEntity<List<PreparedData>> getPreparedData() {
        List<PreparedData> data = preparedDataRepository.findAll();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/customer-categories")
    public ResponseEntity<List<CustumerCateg>> getCustomerCategories() {
        List<CustumerCateg> data = customerCategRepository.findAll();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/reseller-categories")
    public ResponseEntity<List<ResellerCateg>> getResellerCategories() {
        List<ResellerCateg> data = resellerCategRepository.findAll();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/product-categories")
    public ResponseEntity<List<ProductCateg>> getProductCategories() {
        List<ProductCateg> data = productCategRepository.findAll();
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/reseller-without-2nd-reseller")
    public ResponseEntity<List<ResellerWithOut2ndReseller>> getResellerWithOut2ndReseller() {
        List<ResellerWithOut2ndReseller> data = resellerWithOut2ndResellerRepository.findAll();
        return ResponseEntity.ok(data);
    }

    // ========== ENDPOINTS D'UPLOAD (POST) ==========

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<SalesData> dataList = excelReader.readExcelFile(file.getInputStream());
            service.saveAll(dataList);
            return ResponseEntity.ok("Fichier importé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
    
    @PostMapping("/upload/customer")
    public ResponseEntity<String> uploadExcel2(@RequestParam("file") MultipartFile file) {
        try {
            List<CustumerCateg> dataList2 = excelReader.readExcelFile2(file.getInputStream());
            service.saveAll2(dataList2);
            dataserviceprepared.prepareData();
            return ResponseEntity.ok("Fichier importé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
    
    @PostMapping("/upload/reseller")
    public ResponseEntity<String> uploadExcelReseller(@RequestParam("file") MultipartFile file) {
        try {
            List<ResellerCateg> dataList3 = excelReader.readExcelFileReseller(file.getInputStream());
            service.saveAllReseller(dataList3);
            dataserviceprepared.prepareData();
            return ResponseEntity.ok("Fichier importé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
    
    @PostMapping("/upload/product")
    public ResponseEntity<String> uploadExcelProduct(@RequestParam("file") MultipartFile file) {
        try {
            List<ProductCateg> dataList3 = excelReader.readExcelFileProduct(file.getInputStream());
            service.saveAllProduct(dataList3);
            return ResponseEntity.ok("Fichier importé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
}