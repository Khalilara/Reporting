package com.demo.Repository.Channel;

import com.demo.Model.Channel.PreparedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreparedDataRepository extends JpaRepository<PreparedData, Long> {
    @Query("SELECT DISTINCT p.secondReseller FROM PreparedData p " +
            "WHERE p.secondReseller IS NOT NULL AND p.secondReseller != '' " +
            "AND (p.resellerTypeName IS NULL OR p.resellerTypeName = '' " +
            "OR p.channel IS NULL OR p.channel = '')")
    List<String> findDistinctSecondResellersWithMissingInfo();

    @Query("SELECT DISTINCT p.endCustomer FROM PreparedData p " +
            "WHERE p.endCustomer IS NOT NULL AND p.endCustomer != '' " +
            "AND (p.customerType IS NULL OR p.customerType = '')")
    List<String> findDistinctEndCustomersWithMissingType();
}