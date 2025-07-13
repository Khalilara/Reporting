package com.demo.Repository;

import com.demo.Model.ProductCateg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategRepository extends JpaRepository<ProductCateg, Long> {
}
