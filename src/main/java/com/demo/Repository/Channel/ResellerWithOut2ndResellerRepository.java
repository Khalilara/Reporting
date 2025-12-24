package com.demo.Repository.Channel;

import com.demo.Model.Channel.ResellerWithOut2ndReseller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResellerWithOut2ndResellerRepository extends JpaRepository<ResellerWithOut2ndReseller, Long> {
    
    // Trouver le mapping par reseller
    Optional<ResellerWithOut2ndReseller> findByReseller(String reseller);
    
    // Vérifier si un reseller existe déjà
    boolean existsByReseller(String reseller);
    
    // Supprimer par reseller (pour update/remplacement)
    void deleteByReseller(String reseller);
}
