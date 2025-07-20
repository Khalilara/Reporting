package com.demo.DTO;

import java.math.BigDecimal;

public class ChannelRevenueDTO {
    private String channel;
    private BigDecimal revenue;
    private BigDecimal target;

    // Constructeurs, getters, setters

    public ChannelRevenueDTO(String channel, BigDecimal revenue, BigDecimal target) {
        this.channel = channel;
        this.revenue = revenue;
        this.target = target;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }
}

