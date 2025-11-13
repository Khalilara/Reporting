package com.demo.service.Channel;

import com.demo.Model.Channel.CustumerCateg;
import com.demo.Model.Channel.ProductCateg;
import com.demo.Model.Channel.ResellerCateg;
import com.demo.Model.Channel.SalesData;
import com.demo.Repository.Channel.*;
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
    @Autowired
    private PreparedDataRepository preparedDataRepository;

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
    public List<String> getSecondResellersWithMissingInfo() {
        return preparedDataRepository.findDistinctSecondResellersWithMissingInfo();
    }
    public List<String> getEndCustomersWithMissingType() {
        return preparedDataRepository.findDistinctEndCustomersWithMissingType();
    }
}
