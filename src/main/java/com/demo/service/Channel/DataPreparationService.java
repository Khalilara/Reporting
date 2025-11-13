package com.demo.service.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DataPreparationService {
    @Autowired
    private SalesDataRepository salesDataRepository;
    @Autowired
    private ResellerCategRepository resellerCategRepository;
    @Autowired
    private CustomerCategRepository customerCategRepository;
    @Autowired
    private ProductCategRepository productCategRepository;
    @Autowired
    private PreparedDataRepository preparedDataRepository;

    @Transactional
    public void prepareData() {
        preparedDataRepository.deleteAll(); // Reset des données existantes

        List<SalesData> salesDataList = salesDataRepository.findAll();
        List<PreparedData> preparedDataList = new ArrayList<>();

        // SOLUTION 1: Gérer les doublons en gardant la première occurrence
        Map<String, ResellerCateg> resellerMap = resellerCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ResellerCateg::getResellerName,
                        Function.identity(),
                        (existing, replacement) -> existing // Garde la première occurrence
                ));

        Map<String, CustumerCateg> customerMap = customerCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CustumerCateg::getName,
                        Function.identity(),
                        (existing, replacement) -> existing // Garde la première occurrence
                ));

        Map<String, ProductCateg> productMap = productCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ProductCateg::getProductSubSub,
                        Function.identity(),
                        (existing, replacement) -> existing // Garde la première occurrence
                ));

        for (SalesData sales : salesDataList) {
            PreparedData prepared = new PreparedData();

            // Copie des données de base
            prepared.setReseller(sales.getReseller());
            prepared.setResellerType(sales.getResellerType());
            prepared.setSecondReseller(sales.getSecondReseller());
            prepared.setRegion(sales.getRegion());
            prepared.setSubsidiary(sales.getSubsidiary());
            prepared.setEndCustomer(sales.getEndCustomer());
            prepared.setEndCustomerIndustry(sales.getEndCustomerIndustry());
            prepared.setProdSubdinary(sales.getProdSubdinary());
            prepared.setProdSubdinarySubdinary(sales.getProdSubdinarySubdinary());
            prepared.setLicense(sales.getLicense());
            prepared.setYear(sales.getYear());
            prepared.setMonth(sales.getMonth());
            prepared.setRevenue(sales.getRevenue());
            prepared.setLicenceQuantity(sales.getLicenceQuantity());
            prepared.setDiscountRate(sales.getDiscountRate());
            prepared.setBeforeDiscount(sales.getBeforeDiscount());

            // Jointure avec ResellerCateg
            if (sales.getSecondReseller() != null) {
                ResellerCateg reseller = resellerMap.get(sales.getSecondReseller());
                if (reseller != null) {
                    prepared.setResellerTypeName(reseller.getResellerTypeName());
                    prepared.setChannel(reseller.getChannel());
                }
            }

            // Jointure avec CustomerCateg
            if (sales.getEndCustomer() != null) {
                CustumerCateg customer = customerMap.get(sales.getEndCustomer());
                if (customer != null) {
                    prepared.setCustomerType(customer.getCategory());
                }
            }

            // Jointure avec ProductCateg
            if (sales.getProdSubdinarySubdinary() != null) {
                ProductCateg product = productMap.get(sales.getProdSubdinarySubdinary());
                if (product != null) {
                    prepared.setProductType(product.getProductType());
                }
            }

            preparedDataList.add(prepared);
        }

        preparedDataRepository.saveAll(preparedDataList);
    }

    public List<String> getSecondResellersWithMissingInfo() {
        return preparedDataRepository.findDistinctSecondResellersWithMissingInfo();
    }
     public List<String> getEverythingMissing(){
        return preparedDataRepository.findDealEverythingMissing();
    }
     @Transactional
    public int updateResellerData(String reseller, String secondReseller, String resellerTypeName) {
        return preparedDataRepository.updateResellerFields(reseller, secondReseller, resellerTypeName);
    }
}