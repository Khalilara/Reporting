package com.demo.Controller.EBT;


import com.demo.service.EBT.EbtDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(
    origins = "http://localhost",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/ebt/dashbaord")
public class DashboardController {

    @Autowired
    private EbtDashboardService ebtDashboardService;

    @GetMapping("/CA")
    public ResponseEntity<Map<String, Map<String, Double>>> getCA() {
        try {
            return ebtDashboardService.getCA();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/proba")
    public ResponseEntity<Map<String, Map<String, Double>>> getProba() {
        try{
            return   ebtDashboardService.getProba();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/proba-cumuler")
    public ResponseEntity<Map<String, Map<String, Double>>> getProbaCumuler() {
        try{
            return ebtDashboardService.getProbaCumuler();
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
