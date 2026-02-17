package com.demo.Repository.Channel.SLM;

import com.demo.Model.Channel.SLM.EndCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndCustomerRepository extends JpaRepository<EndCustomer, Long> {
    Optional<EndCustomer> findByLicenceKey(String licenceKey);
}
