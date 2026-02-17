package com.demo.Repository.Channel.SLM;

import com.demo.Model.Channel.SLM.SLM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;


public interface SlmRepository extends JpaRepository<SLM, Long> {

    @Query("SELECT s FROM SLM s WHERE (s.clientType IS NULL OR s.clientType = '') AND s.endCustomer IS NOT NULL")
    List<SLM> findAllWithMissingClientType();
    // AJOUTE dans SlmRepository.java
    @Query("SELECT SUM(s.totalPrice) FROM SLM s WHERE s.licenceKey = :licenceKey AND s.clientType = :clientType")
    Double findTotalPriceByLicenceKeyAndClientType(@Param("licenceKey") String licenceKey, @Param("clientType") String clientType);

    @Query("SELECT SUM(s.totalPrice) FROM SLM s WHERE s.clientType = :clientType")
    Double findTotalRevenueByClientType(@Param("clientType") String clientType);

}
