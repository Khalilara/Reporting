package com.demo.Repository;

import com.demo.Model.PreparedData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreparedDataRepository extends JpaRepository<PreparedData, Long> {
}