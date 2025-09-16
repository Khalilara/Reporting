package com.demo.Repository.Channel;

import com.demo.Model.Channel.TopReseller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopResellerRepository extends JpaRepository<TopReseller, Long> {
    Optional<TopReseller> findByName(String name);
}