package com.demo.Model.Channel;


import jakarta.persistence.*;


@Entity
@Table(name="reseller_category")
public class ResellerCateg {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ResellerTypeName;

    private String channel;
    private String resellerName;


    public String getResellerName() {
        return resellerName;
    }

    public void setResellerName(String resellerName) {
        this.resellerName = resellerName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getResellerTypeName() {
        return ResellerTypeName;
    }

    public void setResellerTypeName(String resellerTypeName) {
        ResellerTypeName = resellerTypeName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
