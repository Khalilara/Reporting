package com.demo.service.Channel;

import com.demo.Model.Channel.ResellerWithOut2ndReseller;
import com.demo.Repository.Channel.ResellerWithOut2ndResellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ResellerWithOut2ndResellerService {
    
    @Autowired
    private ResellerWithOut2ndResellerRepository repository;
    
    /**
     * Crée ou met à jour un mapping reseller → secondReseller
     */
    @Transactional
    public ResellerWithOut2ndReseller saveOrUpdate(String reseller, String secondReseller, 
                                                    String resellerTypeName, String channel) {
        // Supprimer l'ancien mapping s'il existe
        if (repository.existsByReseller(reseller)) {
            repository.deleteByReseller(reseller);
        }
        
        // Créer et sauvegarder le nouveau
        ResellerWithOut2ndReseller mapping = new ResellerWithOut2ndReseller(
            reseller, secondReseller, resellerTypeName, channel);
        return repository.save(mapping);
    }
    
    /**
     * Cherche le mapping pour un reseller
     */
    public Optional<ResellerWithOut2ndReseller> findByReseller(String reseller) {
        return repository.findByReseller(reseller);
    }
    
    /**
     * Vérifie si un reseller a un mapping
     */
    public boolean hasMapping(String reseller) {
        return repository.existsByReseller(reseller);
    }
    
    /**
     * Supprime un mapping
     */
    @Transactional
    public void deleteByReseller(String reseller) {
        repository.deleteByReseller(reseller);
    }
}
