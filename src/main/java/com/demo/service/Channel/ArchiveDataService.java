package com.demo.service.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for archiving cleaned PreparedData
 * This is a MANUAL archive triggered by user after data cleaning
 * Workflow:
 * 1. User imports Excel and cleans data in PreparedData
 * 2. User clicks "Archive this week" button
 * 3. This service copies cleaned PreparedData to archive with metadata
 * 4. Old archive for same week/quarter/year is deleted
 */
@Service
public class ArchiveDataService {
    
    @Autowired
    private PreparedDataRepository preparedDataRepository;
    
    @Autowired
    private SalesDataArchiveRepository archiveRepository;
    
    /**
     * Archive cleaned PreparedData to archive table
     * @param year Archive year (e.g., 2026)
     * @param quarter Archive quarter (Q1, Q2, Q3, Q4)
     * @param week Archive week (1-15)
     * @param weekDate Optional week date (e.g., 2026-02-06)
     * @return Archive result with statistics
     */
    @Transactional
    public ArchiveResult archiveCurrentPreparedData(
            Integer year,
            String quarter,
            Integer week,
            String weekDate) {
        
        UUID batchId = UUID.randomUUID();
        int archivedCount = 0;
        boolean isOverwrite = false;
        
        try {
            // Get current PreparedData to archive
            List<PreparedData> currentData = preparedDataRepository.findAll();
            
            if (currentData.isEmpty()) {
                return new ArchiveResult(
                    batchId,
                    0,
                    false,
                    "⚠️ No data to archive - PreparedData is empty"
                );
            }
            
            // Check if archive already exists for this combination
            List<SalesDataArchive> existingArchives = archiveRepository
                .findByArchiveYearAndArchiveQuarterAndArchiveWeek(year, quarter, week);
            
            if (!existingArchives.isEmpty()) {
                // Delete old version - we're replacing with the cleaned version
                archiveRepository.deleteAll(existingArchives);
                isOverwrite = true;
            }
            
            // Convert cleaned PreparedData to archive entries
            List<SalesDataArchive> archiveEntries = currentData.stream()
                .map(pd -> {
                    SalesDataArchive archive = new SalesDataArchive(pd, year, quarter, week, batchId);
                    // Add week date if provided
                    if (weekDate != null && !weekDate.isEmpty()) {
                        try {
                            archive.setWeekDate(LocalDate.parse(weekDate));
                        } catch (Exception e) {
                            // If date parsing fails, leave it null
                        }
                    }
                    return archive;
                })
                .toList();
            
            archiveRepository.saveAll(archiveEntries);
            archivedCount = archiveEntries.size();
            
            String statusMessage = isOverwrite 
                ? "✅ UPDATED - Previous archive replaced with cleaned data"
                : "✅ ARCHIVED - Data archived successfully";
            
            return new ArchiveResult(
                batchId,
                archivedCount,
                isOverwrite,
                String.format("%s | Archived %d cleaned rows for %s %d Week %d | Batch: %s",
                    statusMessage, archivedCount, quarter, year, week, batchId)
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Archive failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * DTO for archive results
     */
    public static class ArchiveResult {
        public UUID batchId;
        public int archivedCount;
        public boolean isOverwrite;
        public String message;
        
        public ArchiveResult(UUID batchId, int archivedCount, boolean isOverwrite, String message) {
            this.batchId = batchId;
            this.archivedCount = archivedCount;
            this.isOverwrite = isOverwrite;
            this.message = message;
        }
    }
}
