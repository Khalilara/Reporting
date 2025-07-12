package com.demo.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "sales_data")
public class SalesData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public String getReseller() {
        return reseller;
    }

    public String getResellerType() {
        return resellerType;
    }

    public String getSecondReseller() {
        return secondReseller;
    }

    public String getRegion() {
        return region;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public String getEndCustomer() {
        return endCustomer;
    }

    public String getEndCustomerIndustry() {
        return endCustomerIndustry;
    }

    public String getProdSubdinary() {
        return prodSubdinary;
    }

    public String getProdSubdinarySubdinary() {
        return prodSubdinarySubdinary;
    }

    public String getLicense() {
        return license;
    }

    public BigDecimal getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public BigDecimal getLicenceQuantity() {
        return licenceQuantity;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public BigDecimal getBeforeDiscount() {
        return beforeDiscount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setReseller(String reseller) {
        this.reseller = reseller;
    }

    public void setResellerType(String resellerType) {
        this.resellerType = resellerType;
    }

    public void setSecondReseller(String secondReseller) {
        this.secondReseller = secondReseller;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public void setEndCustomer(String endCustomer) {
        this.endCustomer = endCustomer;
    }

    public void setEndCustomerIndustry(String endCustomerIndustry) {
        this.endCustomerIndustry = endCustomerIndustry;
    }

    public void setProdSubdinary(String prodSubdinary) {
        this.prodSubdinary = prodSubdinary;
    }

    public void setProdSubdinarySubdinary(String prodSubdinarySubdinary) {
        this.prodSubdinarySubdinary = prodSubdinarySubdinary;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setYear(BigDecimal year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public void setLicenceQuantity(BigDecimal licenceQuantity) {
        this.licenceQuantity = licenceQuantity;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public void setBeforeDiscount(BigDecimal beforeDiscount) {
        this.beforeDiscount = beforeDiscount;
    }

}
