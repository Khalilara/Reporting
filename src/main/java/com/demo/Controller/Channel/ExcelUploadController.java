package com.demo.Controller.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.PreparedDataRepository;
import com.demo.service.Channel.SalesDataService;
import com.demo.service.Channel.ExcelServiceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {
    @Autowired
    private SalesDataService service;

    @Autowired
    private ExcelServiceReader excelReader;
    @Autowired
    private PreparedDataRepository preparedDataRepository;

    @GetMapping("/Hello")
    public ResponseEntity<String> Hello() {
        return ResponseEntity.ok("Hello depuis le contrôleur !");
    }

    @GetMapping("/perpared-data")
    public ResponseEntity<List<PreparedData>> getPreparedData(){
        List<PreparedData> data = preparedDataRepository.findAll();
        return ResponseEntity.ok(data);
    }

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