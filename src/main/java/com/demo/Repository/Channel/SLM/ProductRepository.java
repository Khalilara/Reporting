package com.demo.Repository.Channel.SLM;

import com.demo.Model.Channel.SLM.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByModelName(String modelName);
}
