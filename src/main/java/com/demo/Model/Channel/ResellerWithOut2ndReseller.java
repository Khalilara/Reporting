package com.demo.Model.Channel;

import jakarta.persistence.*;

@Entity
@Table(name = "reseller_without_2nd_reseller")
public class ResellerWithOut2ndReseller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String reseller;
    private String secondReseller;
    private String resellerTypeName;
    private String channel;

    public ResellerWithOut2ndReseller() {}

    public ResellerWithOut2ndReseller(String reseller, String secondReseller, 
                                      String resellerTypeName, String channel) {
        this.reseller = reseller;
        this.secondReseller = secondReseller;
        this.resellerTypeName = resellerTypeName;
        this.channel = channel;
    }

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

    public String getSecondReseller() {
        return secondReseller;
    }

    public void setSecondReseller(String secondReseller) {
        this.secondReseller = secondReseller;
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
}
