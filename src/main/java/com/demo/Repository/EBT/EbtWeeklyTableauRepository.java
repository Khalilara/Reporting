package com.demo.Repository.EBT;

import com.demo.Model.EBT.EbtWeeklyTableau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EbtWeeklyTableauRepository extends JpaRepository<EbtWeeklyTableau, Long> {
    List<EbtWeeklyTableau> findByQuarter(String quarter);
}
