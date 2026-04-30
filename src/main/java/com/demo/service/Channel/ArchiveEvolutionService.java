package com.demo.service.Channel;

import com.demo.Model.Channel.SalesDataArchive;
import com.demo.Repository.Channel.SalesDataArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArchiveEvolutionService {
    
    @Autowired
    private SalesDataArchiveRepository archiveRepository;
    
    /**
     * Get revenue evolution by year with customer_type breakdown
     * Yearly = last non-empty week of Q1 + Q2 + Q3 + Q4
     */
    public Map<String, Object> getYearlyEvolution(String customerType) {
        List<SalesDataArchive> allArchives = archiveRepository.findAll();
        Map<Integer, BigDecimal> yearlyRevenue = new LinkedHashMap<>();
        
        Set<Integer> yearsSet = allArchives.stream()
            .map(SalesDataArchive::getArchiveYear)
            .collect(Collectors.toSet());
        
        List<Integer> yearsList = yearsSet.stream().sorted().collect(Collectors.toList());
        
        for (Integer y : yearsList) {
            // Sum of last non-empty week from each quarter
            BigDecimal q1LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q1", customerType);
            BigDecimal q2LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q2", customerType);
            BigDecimal q3LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q3", customerType);
            BigDecimal q4LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q4", customerType);
            
            BigDecimal yRevenue = q1LastWeek.add(q2LastWeek).add(q3LastWeek).add(q4LastWeek);
            yearlyRevenue.put(y, yRevenue);
        }
        
        return formatChartDataBigDecimal(yearlyRevenue, "Year", customerType);
    }
    
    /**
     * Get revenue evolution by semester (S1: Q1-Q2, S2: Q3-Q4)
     * Each semester = last non-empty week of Q1 + last non-empty week of Q2 (for S1)
     *                or last non-empty week of Q3 + last non-empty week of Q4 (for S2)
     */
    public Map<String, Object> getSemesterEvolution(Integer year, String customerType) {
        List<SalesDataArchive> allArchives = archiveRepository.findAll();
        Map<String, BigDecimal> semesterRevenue = new LinkedHashMap<>();
        
        Set<Integer> yearsSet = allArchives.stream()
            .map(SalesDataArchive::getArchiveYear)
            .collect(Collectors.toSet());
        
        List<Integer> yearsList = yearsSet.stream().sorted().collect(Collectors.toList());
        
        for (Integer y : yearsList) {
            if (year != null && !y.equals(year)) continue;
            
            // S1 = last non-empty week of Q1 + last non-empty week of Q2
            BigDecimal q1LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q1", customerType);
            BigDecimal q2LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q2", customerType);
            BigDecimal s1 = q1LastWeek.add(q2LastWeek);
            
            // S2 = last non-empty week of Q3 + last non-empty week of Q4
            BigDecimal q3LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q3", customerType);
            BigDecimal q4LastWeek = getLastNonEmptyWeekRevenue(allArchives, y, "Q4", customerType);
            BigDecimal s2 = q3LastWeek.add(q4LastWeek);
            
            semesterRevenue.put(y + " S1", s1);
            semesterRevenue.put(y + " S2", s2);
        }
        
        return formatChartDataBigDecimal(semesterRevenue, "Semester", customerType);
    }
    
    /**
     * Get revenue evolution by quarter (Q1, Q2, Q3, Q4)
     */
    public Map<String, Object> getQuarterlyEvolution(Integer year, String customerType) {
        List<SalesDataArchive> allArchives = archiveRepository.findAll();
        Map<String, BigDecimal> quarterlyRevenue = new LinkedHashMap<>();
        
        Set<Integer> yearsSet = allArchives.stream()
            .map(SalesDataArchive::getArchiveYear)
            .collect(Collectors.toSet());
        
        List<Integer> yearsList = yearsSet.stream().sorted().collect(Collectors.toList());
        
        for (Integer y : yearsList) {
            if (year != null && !y.equals(year)) continue;
            
            for (String q : List.of("Q1", "Q2", "Q3", "Q4")) {
                final String quarter = q;  // Make final for lambda
                
                // Get the last non-empty week of the quarter (from week 15 to 1)
                BigDecimal qRevenue = BigDecimal.ZERO;
                for (Integer w = 15; w >= 1; w--) {
                    final Integer week = w;
                    BigDecimal weekRevenue = allArchives.stream()
                        .filter(archive -> archive.getArchiveYear().equals(y))
                        .filter(archive -> archive.getArchiveQuarter().equals(quarter))
                        .filter(archive -> archive.getArchiveWeek().equals(week))
                        .filter(archive -> filterByCustomerType(archive, customerType))
                        .map(SalesDataArchive::getRevenue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    if (weekRevenue.compareTo(BigDecimal.ZERO) > 0) {
                        qRevenue = weekRevenue;
                        break; // Stop at the first non-empty week
                    }
                }
                
                quarterlyRevenue.put(y + " " + q, qRevenue);
            }
        }
        
        return formatChartDataBigDecimal(quarterlyRevenue, "Quarter", customerType);
    }
    
    /**
     * Get CUMULATIVE revenue evolution by week (W1-W15)
     * W1 = Week 1, W2 = W1 + W2, W3 = W1 + W2 + W3, etc.
     */
    public Map<String, Object> getWeeklyEvolution(Integer year, String quarter, String customerType) {
        List<SalesDataArchive> allArchives = archiveRepository.findAll();
        Map<String, BigDecimal> weeklyRevenue = new LinkedHashMap<>();
        final String finalQuarter = quarter;  // Make final for lambda
        
        for (Integer w = 1; w <= 15; w++) {
            final Integer week = w;  // Make final for lambda
            BigDecimal wRevenue = allArchives.stream()
                .filter(archive -> archive.getArchiveYear().equals(year))
                .filter(archive -> archive.getArchiveQuarter().equals(finalQuarter))
                .filter(archive -> archive.getArchiveWeek().equals(week))
                .filter(archive -> filterByCustomerType(archive, customerType))
                .map(SalesDataArchive::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            weeklyRevenue.put("Week " + w, wRevenue);
        }
        
        return formatChartDataBigDecimal(weeklyRevenue, "Week", customerType);
    }
    
    /**
     * Get available years from archive
     */
    public List<Integer> getAvailableYears() {
        return archiveRepository.findAll().stream()
            .map(SalesDataArchive::getArchiveYear)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Get available quarters from archive
     */
    public List<String> getAvailableQuarters() {
        return List.of("Q1", "Q2", "Q3", "Q4");
    }
    
    // ==================== HELPER METHODS ====================
    
    private boolean filterByCustomerType(SalesDataArchive archive, String customerType) {
        if ("all".equalsIgnoreCase(customerType)) {
            return true;
        }
        return archive.getCustomerType() != null && 
               archive.getCustomerType().equalsIgnoreCase(customerType);
    }
    
    /**
     * Get the last non-empty week's revenue for a specific quarter
     * After column rename: getBeforeDiscount() returns revenue values
     */
    private BigDecimal getLastNonEmptyWeekRevenue(List<SalesDataArchive> allArchives, 
                                                   Integer year, String quarter, String customerType) {
        for (Integer w = 15; w >= 1; w--) {
            final Integer week = w;
            BigDecimal weekRevenue = allArchives.stream()
                .filter(archive -> archive.getArchiveYear().equals(year))
                .filter(archive -> archive.getArchiveQuarter().equals(quarter))
                .filter(archive -> archive.getArchiveWeek().equals(week))
                .filter(archive -> filterByCustomerType(archive, customerType))
                .map(SalesDataArchive::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (weekRevenue.compareTo(BigDecimal.ZERO) > 0) {
                return weekRevenue;
            }
        }
        return BigDecimal.ZERO; // No non-empty week found
    }
    
    private Map<String, Object> formatChartDataBigDecimal(Map<?, BigDecimal> dataMap, String period, String customerType) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        List<Map<String, Object>> chartData = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<?, BigDecimal> entry : dataMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", entry.getKey().toString());
            item.put("revenue", entry.getValue());
            chartData.add(item);
            total = total.add(entry.getValue());
        }
        
        response.put("period", period);
        response.put("customerType", customerType);
        response.put("data", chartData);
        response.put("total", total);
        
        return response;
    }
}
