package com.demo.Model.Channel;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.JdbcTypeCode;  // ← Alternative plus récente
import org.hibernate.type.SqlTypes;             // ← Pour JdbcTypeCode

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "weekly_evolution")
public class WeeklyEvolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "week_date", nullable = false, unique = true)
    private LocalDate weekDate;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "year")
    private Integer year;

    @Column(name = "global_revenue", precision = 15, scale = 2)
    private BigDecimal globalRevenue;

    @Column(name = "smb_revenue", precision = 15, scale = 2)
    private BigDecimal smbRevenue;

    @Column(name = "ebt_revenue", precision = 15, scale = 2)
    private BigDecimal ebtRevenue;

    @Column(name = "knox_sw_revenue", precision = 15, scale = 2)
    private BigDecimal knoxSwRevenue;

    @Column(name = "service_revenue", precision = 15, scale = 2)
    private BigDecimal serviceRevenue;

    // Pour stocker le breakdown par type de revendeur (Map<String, BigDecimal>)
    @Type(JsonBinaryType.class)
    @Column(name = "reseller_breakdown", columnDefinition = "jsonb")
    private Map<String, BigDecimal> resellerBreakdown;

    // Pour stocker les données des channels avec targets (List<Map<String, Object>>)
    @Type(JsonBinaryType.class)
    @Column(name = "channels_data", columnDefinition = "jsonb")
    private List<Map<String, Object>> channelsData;

    // Pour stocker les targets globaux (Map<String, Object>)
    @Type(JsonBinaryType.class)
    @Column(name = "targets_data", columnDefinition = "jsonb")
    private Map<String, Object> targetsData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_manual")
    private Boolean isManual;
    @Column(name = "custom_week_label")
private String customWeekLabel;  // ex: "W12", "W09", etc.

    // Constructeurs
    public WeeklyEvolution() {}

    // Getters et Setters (identiques)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomWeekLabel() { return customWeekLabel; }
    public void setCustomWeekLabel(String customWeekLabel) { this.customWeekLabel = customWeekLabel; }
    public LocalDate getWeekDate() { return weekDate; }
    public void setWeekDate(LocalDate weekDate) { this.weekDate = weekDate; }

    public Integer getWeekNumber() { return weekNumber; }
    public void setWeekNumber(Integer weekNumber) { this.weekNumber = weekNumber; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getGlobalRevenue() { return globalRevenue; }
    public void setGlobalRevenue(BigDecimal globalRevenue) { this.globalRevenue = globalRevenue; }

    public BigDecimal getSmbRevenue() { return smbRevenue; }
    public void setSmbRevenue(BigDecimal smbRevenue) { this.smbRevenue = smbRevenue; }

    public BigDecimal getEbtRevenue() { return ebtRevenue; }
    public void setEbtRevenue(BigDecimal ebtRevenue) { this.ebtRevenue = ebtRevenue; }

    public BigDecimal getKnoxSwRevenue() { return knoxSwRevenue; }
    public void setKnoxSwRevenue(BigDecimal knoxSwRevenue) { this.knoxSwRevenue = knoxSwRevenue; }

    public BigDecimal getServiceRevenue() { return serviceRevenue; }
    public void setServiceRevenue(BigDecimal serviceRevenue) { this.serviceRevenue = serviceRevenue; }

    public Map<String, BigDecimal> getResellerBreakdown() { return resellerBreakdown; }
    public void setResellerBreakdown(Map<String, BigDecimal> resellerBreakdown) { this.resellerBreakdown = resellerBreakdown; }

    public List<Map<String, Object>> getChannelsData() { return channelsData; }
    public void setChannelsData(List<Map<String, Object>> channelsData) { this.channelsData = channelsData; }

    public Map<String, Object> getTargetsData() { return targetsData; }
    public void setTargetsData(Map<String, Object> targetsData) { this.targetsData = targetsData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsManual() { return isManual; }
    public void setIsManual(Boolean isManual) { this.isManual = isManual; }
}