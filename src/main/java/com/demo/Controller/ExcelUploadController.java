package com.demo.Controller;

import com.demo.Model.CustumerCateg;
import com.demo.Model.ResellerCateg;
import com.demo.Model.SalesData;
import com.demo.service.SalesDataService;
import com.demo.service.ExcelServiceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/Hello")
    public ResponseEntity<String> Hello() {
        return ResponseEntity.ok("Hello depuis le contrôleur !");
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

}
