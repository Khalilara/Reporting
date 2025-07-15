package com.demo.Model;


import jakarta.persistence.*;

@Entity
@Table(name="product_category")
public class ProductCateg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productSubSub;
    private String ProductType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductSubSub() {
        return productSubSub;
    }

    public void setProductSubSub(String productSubSub) {
        this.productSubSub = productSubSub;
    }

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String productType) {
        ProductType = productType;
    }
}
