package com.demo.service;

import com.demo.Model.CustumerCateg;
import com.demo.Model.ResellerCateg;
import com.demo.Model.SalesData;
import com.demo.Repository.CustomerCategRepository;
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

    public void saveAll(List<SalesData> dataList) {
        repository.saveAll(dataList);
    }
    public void saveAll2(List<CustumerCateg> dataList) {
        repository2.saveAll(dataList);
    }
    public void saveAllReseller(List<ResellerCateg> dataList) {
        repositoryReseller.saveAll(dataList);
    }
}
