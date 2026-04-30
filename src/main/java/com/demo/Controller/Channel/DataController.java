package com.demo.Controller.Channel;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.demo.Model.Channel.CustumerCateg;
import com.demo.Model.Channel.PreparedData;
import com.demo.Model.Channel.ProductCateg;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Model.Channel.ResellerWithOut2ndReseller;
import com.demo.Repository.Channel.CustomerCategRepository;
import com.demo.Repository.Channel.PreparedDataRepository;
import com.demo.Repository.Channel.ProductCategRepository;
import com.demo.Repository.Channel.ResellerCategRepository;
import com.demo.service.Channel.DashboardService;
import com.demo.service.Channel.DataPreparationService;
import com.demo.service.Channel.ExcelServiceReader;
import com.demo.service.Channel.ResellerWithOut2ndResellerService;
import com.demo.service.Channel.SalesDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@CrossOrigin(
    origins = "http://106.102.1.60",
    allowCredentials = "true"
)
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
    @Autowired
    private ResellerWithOut2ndResellerService resellerWithOut2ndResellerService;


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
    
    @GetMapping("/revenue-by-customer")
      public ResponseEntity<Map<String, Object>> getRevenueByCustomerTypeAndResellerWithNames() {
        Map<String, Object> data = dashboardService.getRevenueByCustomerTypeAndResellerWithNames();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/revenue-by-customer/archive")
    public ResponseEntity<Map<String, Object>> getRevenueByCustomerTypeAndResellerWithNamesArchive(
            @RequestParam Integer year,
            @RequestParam String quarter,
            @RequestParam Integer week) {
        Map<String, Object> data = dashboardService.getRevenueByCustomerTypeAndResellerWithNamesArchive(year, quarter, week);
        return ResponseEntity.ok(data);
    }



    @PostMapping("/post/reseller")
public ResponseEntity<?> createReseller(@RequestBody ResellerCateg resellerCateg) {
    try {
        // Validation
        if (resellerCateg.getResellerName() == null || resellerCateg.getResellerName().isEmpty()) {
            return ResponseEntity.badRequest().body("Le nom du reseller est obligatoire");
        }
        if (resellerCateg.getChannel() == null || resellerCateg.getChannel().isEmpty()) {
            return ResponseEntity.badRequest().body("Le channel est obligatoire");
        }
        if (resellerCateg.getResellerTypeName() == null || resellerCateg.getResellerTypeName().isEmpty()) {
            return ResponseEntity.badRequest().body("Le type de reseller est obligatoire");
        }

        // ✅ Vérifier si existe déjà
        Optional<ResellerCateg> existingOpt = resellerCategRepository
            .findByResellerName(resellerCateg.getResellerName());
        
        if (existingOpt.isPresent()) {
            // Mettre à jour l'existant
            ResellerCateg existing = existingOpt.get();
            existing.setChannel(resellerCateg.getChannel());
            existing.setResellerTypeName(resellerCateg.getResellerTypeName());
            resellerCategRepository.save(existing);
        } else {
            // Créer un nouveau
            resellerCategRepository.save(resellerCateg);
        }
        
        // ✅ Recréer les données préparées
        dataPreparationService.prepareData();

        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Reseller créé/mis à jour avec succès");
            
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Erreur : " + e.getMessage());
    }
}

/**
 * Create multiple resellers as "unmanaged" type
 * Sets channel = reseller name, type = "unmanaged"
 */
@PostMapping("/resellers/create-unmanaged")
public ResponseEntity<?> createUnmanagedResellers(@RequestBody List<String> resellerNames) {
    try {
        if (resellerNames == null || resellerNames.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "La liste des resellers est vide"
            ));
        }

        int created = 0;
        int updated = 0;

        for (String resellerName : resellerNames) {
            if (resellerName == null || resellerName.trim().isEmpty()) {
                continue;
            }

            Optional<ResellerCateg> existingOpt = resellerCategRepository.findByResellerName(resellerName);
            
            if (existingOpt.isPresent()) {
                // Update existing
                ResellerCateg existing = existingOpt.get();
                existing.setChannel(resellerName);
                existing.setResellerTypeName("unmanaged");
                resellerCategRepository.save(existing);
                updated++;
            } else {
                // Create new
                ResellerCateg newReseller = new ResellerCateg();
                newReseller.setResellerName(resellerName);
                newReseller.setChannel(resellerName);
                newReseller.setResellerTypeName("unmanaged");
                resellerCategRepository.save(newReseller);
                created++;
            }
        }

        // Regenerate prepared data
        dataPreparationService.prepareData();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", String.format("✅ Créé: %d, Mis à jour: %d resellers unmanaged", created, updated),
            "created", created,
            "updated", updated
        ));

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "message", "Erreur : " + e.getMessage()
            ));
    }
}

@PostMapping("/resellers/update")
public ResponseEntity<String> updateResellerData(@RequestBody Map<String, String> payload) {
    try {
        String reseller = payload.get("reseller");
        String secondReseller = payload.get("secondReseller");
        String resellerTypeName = payload.get("resellerTypeName");

        // Validation
        if (reseller == null || reseller.isEmpty()) {
            return ResponseEntity.badRequest().body("Le nom du reseller est obligatoire");
        }
        if (secondReseller == null || secondReseller.isEmpty()) {
            return ResponseEntity.badRequest().body("Le secondReseller est obligatoire");
        }
        if (resellerTypeName == null || resellerTypeName.isEmpty()) {
            return ResponseEntity.badRequest().body("Le resellerTypeName est obligatoire");
        }

        // ✅ 1. Sauvegarder dans ResellerWithOut2ndReseller (persistance)
        resellerWithOut2ndResellerService.saveOrUpdate(
            reseller, secondReseller, resellerTypeName, secondReseller);

        // ✅ 2. Mettre à jour immédiatement PreparedData pour ce reseller
        int updatedCount = dataPreparationService.updatePreparedDataForReseller(
            reseller, secondReseller, resellerTypeName, secondReseller);

        String message = "Reseller \"" + reseller + "\" mappé avec succès au secondReseller \"" + 
                         secondReseller + "\" !";
        if (updatedCount > 0) {
            message += " (" + updatedCount + " enregistrement(s) PreparedData mis à jour immédiatement)";
        }

        return ResponseEntity.ok(message);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur : " + e.getMessage());
    }
}
@PutMapping("/perpared-data/{id}")
public ResponseEntity<?> updatePreparedData(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    try {
        // Récupérer l'entité existante
        PreparedData existingData = preparedDataRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Prepared data non trouvée avec l'ID: " + id));

        // Mise à jour dynamique des champs
        updates.forEach((key, value) -> {
            switch (key) {
                case "reseller":
                    existingData.setReseller(value != null ? value.toString() : null);
                    break;
                case "channel":
                    existingData.setChannel(value != null ? value.toString() : null);
                    break;
                case "resellerTypeName":
                    existingData.setResellerTypeName(value != null ? value.toString() : null);
                    break;
                case "secondReseller":
                    existingData.setSecondReseller(value != null ? value.toString() : null);
                    break;
                case "resellerType":
                    existingData.setResellerType(value != null ? value.toString() : null);
                    break;
                case "region":
                    existingData.setRegion(value != null ? value.toString() : null);
                    break;
                case "subsidiary":
                    existingData.setSubsidiary(value != null ? value.toString() : null);
                    break;
                case "endCustomer":
                    existingData.setEndCustomer(value != null ? value.toString() : null);
                    break;
                case "endCustomerIndustry":
                    existingData.setEndCustomerIndustry(value != null ? value.toString() : null);
                    break;
                case "prodSubdinary":
                    existingData.setProdSubdinary(value != null ? value.toString() : null);
                    break;
                case "prodSubdinarySubdinary":
                    existingData.setProdSubdinarySubdinary(value != null ? value.toString() : null);
                    break;
                case "license":
                    existingData.setLicense(value != null ? value.toString() : null);
                    break;
                case "productType":
                    existingData.setProductType(value != null ? value.toString() : null);
                    break;
                case "year":
                    if (value != null) {
                        if (value instanceof Number) {
                            existingData.setYear(BigDecimal.valueOf(((Number) value).doubleValue()));
                        } else {
                            existingData.setYear(new BigDecimal(value.toString()));
                        }
                    } else {
                        existingData.setYear(null);
                    }
                    break;
                case "month":
                    existingData.setMonth(value != null ? value.toString() : null);
                    break;
                case "revenue":
                    if (value != null) {
                        if (value instanceof Number) {
                            existingData.setRevenue(BigDecimal.valueOf(((Number) value).doubleValue()));
                        } else {
                            existingData.setRevenue(new BigDecimal(value.toString()));
                        }
                    } else {
                        existingData.setRevenue(null);
                    }
                    break;
                case "licenceQuantity":
                    if (value != null) {
                        if (value instanceof Number) {
                            existingData.setLicenceQuantity(BigDecimal.valueOf(((Number) value).doubleValue()));
                        } else {
                            existingData.setLicenceQuantity(new BigDecimal(value.toString()));
                        }
                    } else {
                        existingData.setLicenceQuantity(null);
                    }
                    break;
                case "discountRate":
                    if (value != null) {
                        if (value instanceof Number) {
                            existingData.setDiscountRate(BigDecimal.valueOf(((Number) value).doubleValue()));
                        } else {
                            existingData.setDiscountRate(new BigDecimal(value.toString()));
                        }
                    } else {
                        existingData.setDiscountRate(null);
                    }
                    break;
                case "beforeDiscount":
                    if (value != null) {
                        if (value instanceof Number) {
                            existingData.setBeforeDiscount(BigDecimal.valueOf(((Number) value).doubleValue()));
                        } else {
                            existingData.setBeforeDiscount(new BigDecimal(value.toString()));
                        }
                    } else {
                        existingData.setBeforeDiscount(null);
                    }
                    break;
                case "customerType":
                    existingData.setCustomerType(value != null ? value.toString() : null);
                    break;
            }
        });

        // Sauvegarder les modifications
        PreparedData savedData = preparedDataRepository.save(existingData);
        
        return ResponseEntity.ok(savedData);
        
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Prepared data non trouvée avec l'ID: " + id + ": " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Une erreur est survenue lors de la mise à jour: " + e.getMessage());
    }
}

/**
 * Batch update: Set all empty/null/N/A resellerType to "unmanaged"
 */
@PutMapping("/perpared-data/batch/set-unmanaged")
public ResponseEntity<?> setEmptyResellerTypeToUnmanaged() {
    try {
        // Get all prepared data
        List<PreparedData> allData = preparedDataRepository.findAll();
        
        int updated = 0;
        
        // Update all with empty, null, or "N/A" resellerType
        for (PreparedData item : allData) {
            String resellerType = item.getResellerType();
            if (resellerType == null || 
                resellerType.trim().isEmpty() || 
                resellerType.trim().equalsIgnoreCase("N/A") ||
                resellerType.trim().equalsIgnoreCase("na")) {
                item.setResellerType("unmanaged");
                preparedDataRepository.save(item);
                updated++;
            }
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", String.format("✅ %d resellers set to unmanaged", updated),
            "updated", updated
        ));
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "success", false,
                "message", "Erreur : " + e.getMessage()
            ));
    }
}

}

