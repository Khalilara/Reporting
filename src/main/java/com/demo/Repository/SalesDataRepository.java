package com.demo.Repository;

import com.demo.Model.SalesData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesDataRepository extends JpaRepository<SalesData, Long> {

}
