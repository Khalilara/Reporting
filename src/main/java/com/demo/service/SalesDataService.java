package com.demo.service;

import com.demo.Model.SalesData;
import com.demo.Repository.SalesDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesDataService {

    @Autowired
    private SalesDataRepository repository;

    public void saveAll(List<SalesData> dataList) {
        repository.saveAll(dataList);
    }
}
