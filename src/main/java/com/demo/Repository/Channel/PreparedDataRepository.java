package com.demo.Repository.Channel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


import com.demo.Model.Channel.PreparedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PreparedDataRepository extends JpaRepository<PreparedData, Long> {
    
    // Pour trouver les secondResellers manquants (CAS 1)
    @Query("SELECT DISTINCT p.secondReseller FROM PreparedData p " +
           "WHERE p.secondReseller IS NOT NULL AND p.secondReseller != '' " +
           "AND p.secondReseller != '(empty)' AND p.secondReseller != '(Empty)' " +
           "AND (p.resellerTypeName IS NULL OR p.resellerTypeName = '' " +
           "OR p.channel IS NULL OR p.channel = '')")
    List<String> findDistinctSecondResellersWithMissingInfo();

    // Pour trouver les customers manquants
    @Query("SELECT DISTINCT p.endCustomer FROM PreparedData p " +
           "WHERE p.endCustomer IS NOT NULL AND p.endCustomer != '' " +
           "AND (p.customerType IS NULL OR p.customerType = '')")
    List<String> findDistinctEndCustomersWithMissingType();

    // Pour trouver les resellers où secondReseller est empty (CAS 2)
    @Query("SELECT DISTINCT p.reseller FROM PreparedData p " +
           "WHERE p.secondReseller IS NULL OR p.secondReseller = '' " +
           "OR p.secondReseller = '(empty)' OR p.secondReseller = '(Empty)'")
    List<String> findDealEverythingMissing();

    // ✅ Mise à jour pour le CAS 2 (secondReseller empty)
    @Modifying
    @Transactional
    @Query("""
        UPDATE PreparedData p 
        SET p.secondReseller = :secondReseller, 
            p.resellerTypeName = :resellerTypeName,
            p.channel = :secondReseller
        WHERE p.reseller = :reseller
        """)
    int updateResellerFields(@Param("reseller") String reseller,
                            @Param("secondReseller") String secondReseller,
                            @Param("resellerTypeName") String resellerTypeName);
}