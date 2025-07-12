package com.demo.Repository;

import com.demo.Model.CustumerCateg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCategRepository  extends JpaRepository<CustumerCateg, Long> {
}
