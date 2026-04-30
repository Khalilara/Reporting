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
 * Service for importing sales data with archiving support
 * Workflow:
 * 1. Archive current PreparedData with metadata (year, quarter, week)
 * 2. Update PreparedData with new data
 */
@Service
public class SalesDataImportService {
    
    @Autowired
    private SalesDataRepository salesDataRepository;
    
    @Autowired
    private PreparedDataRepository preparedDataRepository;
    
    @Autowired
    private SalesDataArchiveRepository archiveRepository;
    
    @Autowired
    private CustomerCategRepository customerCategRepository;
    
    @Autowired
    private ResellerCategRepository resellerCategRepository;
    
    @Autowired
    private ProductCategRepository productCategRepository;
    
    @Autowired
    private DataPreparationService dataPreparationService;
    
    /**
     * Import sales data WITHOUT archiving (new workflow)
     * Archiving is now a separate manual step via ArchiveDataService
     * 
     * This method:
     * 1. Clears sales_data table
     * 2. Imports Excel data to sales_data
     * 3. Clears prepared_data table
     * 4. Regenerates prepared_data via dataPreparationService
     * 
     * NOTE: Metadata (year, quarter, week, weekDate) are IGNORED here
     * They are now collected during the manual archiving step in Data Table
     * 
     * @param dataList New sales data from Excel
     * @param year IGNORED - kept for backward compatibility
     * @param quarter IGNORED - kept for backward compatibility
     * @param week IGNORED - kept for backward compatibility
     * @param weekDate IGNORED - kept for backward compatibility
     * @return Import result with statistics
     */
    @Transactional
    public ImportResult importSalesData(
            List<SalesData> dataList,
            Integer year,
            String quarter,
            Integer week,
            String weekDate) {
        
        UUID batchId = UUID.randomUUID();
        
        try {
            // ========== STEP 1: Clear and update sales_data ==========
            salesDataRepository.deleteAll();
            salesDataRepository.saveAll(dataList);
            
            // ========== STEP 2: Clear and regenerate PreparedData ==========
            preparedDataRepository.deleteAll();
            dataPreparationService.prepareData();
            
            String statusMessage = "✅ NEW - Data imported and ready for cleaning";
            
            return new ImportResult(
                batchId,
                dataList.size(),
                0,
                String.format("%s | Imported %d rows | Archive manually after cleaning in Data Table",
                    statusMessage, dataList.size())
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Import failed: " + e.getMessage(), e);
        }
    }

    /**
     * Legacy method for backward compatibility (without weekDate)
     */
    @Transactional
    public ImportResult importSalesData(
            List<SalesData> dataList,
            Integer year,
            String quarter,
            Integer week) {
        return importSalesData(dataList, year, quarter, week, null);
    }
    
    public void saveAll2(List<CustumerCateg> dataList) {
        customerCategRepository.saveAll(dataList);
    }
    
    public void saveAllReseller(List<ResellerCateg> dataList) {
        resellerCategRepository.saveAll(dataList);
    }
    
    public void saveAllProduct(List<ProductCateg> dataList) {
        productCategRepository.saveAll(dataList);
    }
    
    public List<String> getSecondResellersWithMissingInfo() {
        return preparedDataRepository.findDistinctSecondResellersWithMissingInfo();
    }
    
    public List<String> getEndCustomersWithMissingType() {
        return preparedDataRepository.findDistinctEndCustomersWithMissingType();
    }
    
    /**
     * DTO for import results
     */
    public static class ImportResult {
        public UUID batchId;
        public int importedRows;
        public int archivedRows;
        public String message;
        
        public ImportResult(UUID batchId, int importedRows, int archivedRows, String message) {
            this.batchId = batchId;
            this.importedRows = importedRows;
            this.archivedRows = archivedRows;
            this.message = message;
        }
    }
}
