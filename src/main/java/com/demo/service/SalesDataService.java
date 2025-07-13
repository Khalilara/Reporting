package com.demo.service;

import com.demo.Model.CustumerCateg;
import com.demo.Model.ProductCateg;
import com.demo.Model.ResellerCateg;
import com.demo.Model.SalesData;
import com.demo.Repository.CustomerCategRepository;
import com.demo.Repository.ProductCategRepository;
import com.demo.Repository.ResellerCategRepository;
import com.demo.Repository.SalesDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesDataService {

    @Autowired
    private SalesDataRepository repository;
    @Autowired
    private CustomerCategRepository repository2;
    @Autowired
    private ResellerCategRepository repositoryReseller;
    @Autowired
    private ProductCategRepository repositoryProduct;
    @Autowired
    private DataPreparationService dataPreparationService;

    public void saveAll(List<SalesData> dataList) {
        repository.saveAll(dataList);
        dataPreparationService.prepareData();
    }
    public void saveAll2(List<CustumerCateg> dataList) {
        repository2.saveAll(dataList);
    }
    public void saveAllReseller(List<ResellerCateg> dataList) {
        repositoryReseller.saveAll(dataList);
    }
    public void saveAllProduct(List<ProductCateg> dataList) {
        repositoryProduct.saveAll(dataList);
    }


}
