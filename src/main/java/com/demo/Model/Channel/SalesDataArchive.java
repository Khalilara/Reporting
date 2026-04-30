package com.demo.Model.Channel;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Archive table for historical versions of PreparedData
 * Each import creates a snapshot with metadata (year, quarter, week)
 */
@Entity
@Table(name = "sales_data_archive")
public class SalesDataArchive {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ========== Original PreparedData columns ==========
    private String reseller;
    private String resellerType;
    private String secondReseller;
    private String region;
    private String subsidiary;
    private String endCustomer;
    private String endCustomerIndustry;
    private String prodSubdinary;
    private String prodSubdinarySubdinary;
    private String license;
    private BigDecimal year;
    private String month;
    private BigDecimal revenue;
    private BigDecimal licenceQuantity;
    private BigDecimal discountRate;
    private BigDecimal beforeDiscount;
    
    // ========== Enriched columns ==========
    private String resellerTypeName;
    private String channel;
    private String customerType;
    private String productType;
    
    // ========== Archive metadata (3 confirmed columns) ==========
    @Column(nullable = false)
    private Integer archiveYear;  // e.g., 2025, 2026
    
    @Column(nullable = false)
    private String archiveQuarter;  // e.g., Q1, Q2, Q3, Q4
    
    @Column(nullable = false)
    private Integer archiveWeek;  // e.g., 1-15 (per quarter = 3 months)
    
    @Column(nullable = true)
    private LocalDate weekDate;  // Week date (e.g., 2026-02-06)
    
    // ========== Audit metadata ==========
    @Column(nullable = false)
    private UUID batchId;  // Unique identifier for this import
    
    @Column(nullable = false)
    private LocalDateTime archivedAt;
    
    // ========== Constructors ==========
    public SalesDataArchive() {
    }
    
    public SalesDataArchive(PreparedData preparedData, Integer archiveYear, 
                           String archiveQuarter, Integer archiveWeek, UUID batchId) {
        // Copy all fields from PreparedData
        this.reseller = preparedData.getReseller();
        this.resellerType = preparedData.getResellerType();
        this.secondReseller = preparedData.getSecondReseller();
        this.region = preparedData.getRegion();
        this.subsidiary = preparedData.getSubsidiary();
        this.endCustomer = preparedData.getEndCustomer();
        this.endCustomerIndustry = preparedData.getEndCustomerIndustry();
        this.prodSubdinary = preparedData.getProdSubdinary();
        this.prodSubdinarySubdinary = preparedData.getProdSubdinarySubdinary();
        this.license = preparedData.getLicense();
        this.year = preparedData.getYear();
        this.month = preparedData.getMonth();
        this.revenue = preparedData.getRevenue();
        this.licenceQuantity = preparedData.getLicenceQuantity();
        this.discountRate = preparedData.getDiscountRate();
        this.beforeDiscount = preparedData.getBeforeDiscount();
        
        // Copy enriched columns
        this.resellerTypeName = preparedData.getResellerTypeName();
        this.channel = preparedData.getChannel();
        this.customerType = preparedData.getCustomerType();
        this.productType = preparedData.getProductType();
        
        // Set archive metadata
        this.archiveYear = archiveYear;
        this.archiveQuarter = archiveQuarter;
        this.archiveWeek = archiveWeek;
        this.batchId = batchId;
        this.archivedAt = LocalDateTime.now();
    }
    
    // ========== Getters and Setters ==========
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getReseller() {
        return reseller;
    }
    
    public void setReseller(String reseller) {
        this.reseller = reseller;
    }
    
    public String getResellerType() {
        return resellerType;
    }
    
    public void setResellerType(String resellerType) {
        this.resellerType = resellerType;
    }
    
    public String getSecondReseller() {
        return secondReseller;
    }
    
    public void setSecondReseller(String secondReseller) {
        this.secondReseller = secondReseller;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getSubsidiary() {
        return subsidiary;
    }
    
    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }
    
    public String getEndCustomer() {
        return endCustomer;
    }
    
    public void setEndCustomer(String endCustomer) {
        this.endCustomer = endCustomer;
    }
    
    public String getEndCustomerIndustry() {
        return endCustomerIndustry;
    }
    
    public void setEndCustomerIndustry(String endCustomerIndustry) {
        this.endCustomerIndustry = endCustomerIndustry;
    }
    
    public String getProdSubdinary() {
        return prodSubdinary;
    }
    
    public void setProdSubdinary(String prodSubdinary) {
        this.prodSubdinary = prodSubdinary;
    }
    
    public String getProdSubdinarySubdinary() {
        return prodSubdinarySubdinary;
    }
    
    public void setProdSubdinarySubdinary(String prodSubdinarySubdinary) {
        this.prodSubdinarySubdinary = prodSubdinarySubdinary;
    }
    
    public String getLicense() {
        return license;
    }
    
    public void setLicense(String license) {
        this.license = license;
    }
    
    public BigDecimal getYear() {
        return year;
    }
    
    public void setYear(BigDecimal year) {
        this.year = year;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
    }
    
    public BigDecimal getRevenue() {
        return revenue;
    }
    
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
    
    public BigDecimal getLicenceQuantity() {
        return licenceQuantity;
    }
    
    public void setLicenceQuantity(BigDecimal licenceQuantity) {
        this.licenceQuantity = licenceQuantity;
    }
    
    public BigDecimal getDiscountRate() {
        return discountRate;
    }
    
    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }
    
    public BigDecimal getBeforeDiscount() {
        return beforeDiscount;
    }
    
    public void setBeforeDiscount(BigDecimal beforeDiscount) {
        this.beforeDiscount = beforeDiscount;
    }
    
    public String getResellerTypeName() {
        return resellerTypeName;
    }
    
    public void setResellerTypeName(String resellerTypeName) {
        this.resellerTypeName = resellerTypeName;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    // ========== Archive metadata getters/setters ==========
    public Integer getArchiveYear() {
        return archiveYear;
    }
    
    public void setArchiveYear(Integer archiveYear) {
        this.archiveYear = archiveYear;
    }
    
    public String getArchiveQuarter() {
        return archiveQuarter;
    }
    
    public void setArchiveQuarter(String archiveQuarter) {
        this.archiveQuarter = archiveQuarter;
    }
    
    public Integer getArchiveWeek() {
        return archiveWeek;
    }
    
    public void setArchiveWeek(Integer archiveWeek) {
        this.archiveWeek = archiveWeek;
    }
    
    public LocalDate getWeekDate() {
        return weekDate;
    }
    
    public void setWeekDate(LocalDate weekDate) {
        this.weekDate = weekDate;
    }
    
    public UUID getBatchId() {
        return batchId;
    }
    
    public void setBatchId(UUID batchId) {
        this.batchId = batchId;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
}
