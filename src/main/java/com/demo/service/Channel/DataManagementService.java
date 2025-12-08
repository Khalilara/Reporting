package com.demo.service.Channel;


import com.demo.Repository.Channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataManagementService{

    @Autowired
    private SalesDataRepository salesDataRepository;
    
    @Autowired
    private PreparedDataRepository preparedDataRepository;
    
    @Autowired
    private ResellerCategRepository resellerCategRepository;
    
    @Autowired
    private TopResellerRepository topResellerRepository;
    @Autowired
    private ProductCategRepository productCategRepository;
    
    @Transactional
    public Map<String, Long> clearAllData() {
        Map<String, Long> deletedCounts = new HashMap<>();
        
        // Supprimer dans l'ordre (pour respecter les contraintes FK si elles existent)
        deletedCounts.put("PreparedData", preparedDataRepository.count());
        preparedDataRepository.deleteAll();
        
        deletedCounts.put("SalesData", salesDataRepository.count());
        salesDataRepository.deleteAll();
        
        deletedCounts.put("ResellerCateg", resellerCategRepository.count());
        resellerCategRepository.deleteAll();
        
        deletedCounts.put("TopReseller", topResellerRepository.count());
        topResellerRepository.deleteAll();
        
        deletedCounts.put("ProductCateg", productCategRepository.count());
        topResellerRepository.deleteAll();
        
        return deletedCounts;
    }
}
