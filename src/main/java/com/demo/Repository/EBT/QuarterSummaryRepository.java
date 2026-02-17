package com.demo.Repository.EBT;

import com.demo.Model.EBT.QuarterSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuarterSummaryRepository extends JpaRepository<QuarterSummary, Long> {
    Optional<QuarterSummary> findByQuarter(String quarter);
}
