package com.demo.Repository.Channel;

import com.demo.Model.Channel.SalesDataArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalesDataArchiveRepository extends JpaRepository<SalesDataArchive, Long> {
    
    /**
     * Find all archived versions for a specific period
     */
    List<SalesDataArchive> findByArchiveYearAndArchiveQuarterAndArchiveWeek(
        Integer year, String quarter, Integer week
    );
    
    /**
     * Find all archived versions for a specific quarter/year
     */
    List<SalesDataArchive> findByArchiveYearAndArchiveQuarter(
        Integer year, String quarter
    );
    
    /**
     * Find all archived versions for a specific year
     */
    List<SalesDataArchive> findByArchiveYear(Integer year);
    
    /**
     * Find archived version by batch ID
     */
    List<SalesDataArchive> findByBatchId(UUID batchId);
    
    /**
     * Check if a specific period already exists (to prevent duplicates)
     */
    @Query("SELECT COUNT(a) > 0 FROM SalesDataArchive a " +
           "WHERE a.archiveYear = :year " +
           "AND a.archiveQuarter = :quarter " +
           "AND a.archiveWeek = :week")
    boolean existsByPeriod(
        @Param("year") Integer year,
        @Param("quarter") String quarter,
        @Param("week") Integer week
    );
}
