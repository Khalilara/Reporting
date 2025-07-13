package com.demo.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "prepared_data")
public class PreparedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SalesData columns
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

    // Joined columns
    private String resellerTypeName;
    private String channel;
    private String customerType;
    private String productType;

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
}
