package com.demo.Controller.Channel;


import com.demo.Model.Channel.CustumerCateg;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.ProductCateg;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Repository.Channel.CustomerCategRepository;
import com.demo.Repository.Channel.PreparedDataRepository;
import com.demo.Repository.Channel.ProductCategRepository;
import com.demo.Repository.Channel.ResellerCategRepository;
import com.demo.service.Channel.DashboardService;
import com.demo.service.Channel.DataPreparationService;
import com.demo.service.Channel.ExcelServiceReader;
import com.demo.service.Channel.SalesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class DataController {
    @Autowired
    private SalesDataService service;
    @Autowired
    private ExcelServiceReader excelReader;
    @Autowired
    private PreparedDataRepository preparedDataRepository;
    @Autowired
    private CustomerCategRepository customerCategRepository;
    @Autowired
    private ResellerCategRepository resellerCategRepository;
    @Autowired
    private ProductCategRepository productCategRepository;
    @Autowired
    private DataPreparationService dataPreparationService;
    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/perpared-data")
    public ResponseEntity<List<PreparedData>> getPreparedData(){
        List<PreparedData> data = preparedDataRepository.findAll();
        return ResponseEntity.ok(data);
    }
    @PostMapping("/post/customer")
    public ResponseEntity<?> createCustomer(@RequestBody CustumerCateg customer) {
        try {
            // Validation simple
            if (customer.getName() == null || customer.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Le nom du customer est obligatoire");
            }

            if (customer.getCategory() == null || customer.getCategory().isEmpty()) {
                return ResponseEntity.badRequest().body("La catégorie du customer est obligatoire");
            }

            // Sauvegarder le nouveau customer
            CustumerCateg savedCustomer = customerCategRepository.save(customer);
            dataPreparationService.prepareData();


            // Retourner le customer créé avec le statut 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du customer: " + e.getMessage());
        }
    }

    @PostMapping("/post/reseller")
    public ResponseEntity<?> createReseller(@RequestBody ResellerCateg resellerCateg) {
        try {
            // Validation simple
            if (resellerCateg.getResellerName() == null || resellerCateg.getResellerName().isEmpty()) {
                return ResponseEntity.badRequest().body("Le nom du customer est obligatoire");
            }
            if (resellerCateg.getChannel() == null || resellerCateg.getChannel().isEmpty()) {
                return ResponseEntity.badRequest().body("Le nom du customer est obligatoire");
            }

            if (resellerCateg.getResellerTypeName() == null || resellerCateg.getResellerTypeName().isEmpty()) {
                return ResponseEntity.badRequest().body("La catégorie du customer est obligatoire");
            }

            // Sauvegarder le nouveau customer
            ResellerCateg savedReseller = resellerCategRepository.save(resellerCateg);
            dataPreparationService.prepareData();


            // Retourner le customer créé avec le statut 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReseller);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du customer: " + e.getMessage());
        }
    }
    @PostMapping("/post/product")
    public ResponseEntity<?> createProduct(@RequestBody ProductCateg productCateg) {
        try {
            // Validation simple
            if (productCateg.getProductSubSub() == null || productCateg.getProductSubSub().isEmpty()) {
                return ResponseEntity.badRequest().body("Le nom du customer est obligatoire");
            }

            if (productCateg.getProductType() == null || productCateg.getProductType().isEmpty()) {
                return ResponseEntity.badRequest().body("La catégorie du customer est obligatoire");
            }

            // Sauvegarder le nouveau customer
            ProductCateg savedProduct = productCategRepository.save(productCateg);
            dataPreparationService.prepareData();


            // Retourner le customer créé avec le statut 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du customer: " + e.getMessage());
        }
    }
    @GetMapping("/resellers/missing")
    public ResponseEntity<List<String>> getSecondResellersWithMissingInfo() {
        try {
            List<String> resellers = service.getSecondResellersWithMissingInfo();
            return ResponseEntity.ok(resellers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
}
    @GetMapping("/customers/missing")
    public ResponseEntity<List<String>> getEndCustomersWithMissingType() {
        try {
            List<String> customers = service.getEndCustomersWithMissingType();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @GetMapping("/all/missing")
    public ResponseEntity<List<String>> getEveryThingMissing(){
        try{
            List<String> deals = dataPreparationService.getEverythingMissing();
            return ResponseEntity.ok(deals);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(null);
        }
    }
    @PostMapping("/resellers/update")
    public ResponseEntity<String> updateResellerData(@RequestBody Map<String, String> payload) {
    try {
        String reseller = payload.get("reseller");
        String secondReseller = payload.get("secondReseller");
        String resellerTypeName = payload.get("resellerTypeName");

        int updated = dataPreparationService.updateResellerData(reseller, secondReseller, resellerTypeName);

        if (updated > 0) {
            return ResponseEntity.ok("Reseller mis à jour avec succès !");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun enregistrement trouvé pour ce reseller.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur : " + e.getMessage());
    }
   }
    @GetMapping("/revenue-by-customer")
      public ResponseEntity<Map<String, Object>> getRevenueByCustomerTypeAndResellerWithNames() {
        Map<String, Object> data = dashboardService.getRevenueByCustomerTypeAndResellerWithNames();
        return ResponseEntity.ok(data);
    }

}

