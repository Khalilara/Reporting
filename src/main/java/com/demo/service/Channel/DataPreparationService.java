package com.demo.service.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private ResellerWithOut2ndResellerRepository resellerWithOut2ndResellerRepository;

    @Transactional
    public void prepareData() {
        System.out.println("Suppression prepared_data...");
        preparedDataRepository.deleteAll();

        System.out.println("Chargement sales_data...");
        List<SalesData> salesDataList = salesDataRepository.findAll();

        System.out.println("Chargement tables de correspondance...");

        Map<String, ResellerCateg> resellerMap = resellerCategRepository.findAll()
                .stream()
                .filter(r -> r.getResellerName() != null)
                .collect(Collectors.toMap(
                        r -> cleanKey(r.getResellerName()),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, CustumerCateg> customerMap = customerCategRepository.findAll()
                .stream()
                .filter(c -> c.getName() != null)
                .collect(Collectors.toMap(
                        c -> cleanKey(c.getName()),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, ProductCateg> productMap = productCategRepository.findAll()
                .stream()
                .filter(p -> p.getProductSubSub() != null)
                .collect(Collectors.toMap(
                        p -> cleanKey(p.getProductSubSub()),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, ResellerWithOut2ndReseller> resellerWithoutSecondMap =
                resellerWithOut2ndResellerRepository.findAll()
                        .stream()
                        .filter(r -> r.getReseller() != null)
                        .collect(Collectors.toMap(
                                r -> cleanKey(r.getReseller()),
                                Function.identity(),
                                (existing, replacement) -> existing
                        ));

        System.out.println("Début préparation : " + salesDataList.size() + " lignes");

        List<PreparedData> batch = new ArrayList<>();
        int success = 0;
        int failed = 0;
        int batchSize = 500;

        for (int i = 0; i < salesDataList.size(); i++) {
            SalesData sales = salesDataList.get(i);

            try {
                PreparedData prepared = new PreparedData();

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

                String secondReseller = cleanKey(sales.getSecondReseller());
                String resellerName = cleanKey(sales.getReseller());
                String endCustomer = cleanKey(sales.getEndCustomer());
                String productSubSub = cleanKey(sales.getProdSubdinarySubdinary());

                if (isNotEmpty(secondReseller)) {
                    ResellerCateg reseller = resellerMap.get(secondReseller);

                    if (reseller != null) {
                        prepared.setResellerTypeName(reseller.getResellerTypeName());
                        prepared.setChannel(reseller.getChannel());
                    }

                } else if (isNotEmpty(resellerName)) {
                    ResellerWithOut2ndReseller mapping = resellerWithoutSecondMap.get(resellerName);

                    if (mapping != null) {
                        prepared.setSecondReseller(mapping.getSecondReseller());
                        prepared.setResellerTypeName(mapping.getResellerTypeName());
                        prepared.setChannel(mapping.getChannel());
                    } else {
                        ResellerCateg reseller = resellerMap.get(resellerName);

                        if (reseller != null) {
                            prepared.setResellerTypeName(reseller.getResellerTypeName());
                            prepared.setChannel(reseller.getChannel());
                        }
                    }
                }

                if (isNotEmpty(endCustomer)) {
                    CustumerCateg customer = customerMap.get(endCustomer);

                    if (customer != null) {
                        prepared.setCustomerType(customer.getCategory());
                    }
                }

                if (isNotEmpty(productSubSub)) {
                    ProductCateg product = productMap.get(productSubSub);

                    if (product != null) {
                        prepared.setProductType(product.getProductType());
                    }
                }

                batch.add(prepared);
                success++;

                if (batch.size() >= batchSize) {
                    preparedDataRepository.saveAll(batch);
                    preparedDataRepository.flush();
                    batch.clear();

                    System.out.println("PreparedData sauvegardées : " + success + " / " + salesDataList.size());
                }

            } catch (Exception e) {
                failed++;

                System.out.println("--------------------------------------");
                System.out.println("Ligne ignorée pendant prepareData : " + (i + 2));
                System.out.println("Erreur : " + e.getMessage());
                System.out.println("--------------------------------------");
            }
        }

        if (!batch.isEmpty()) {
            preparedDataRepository.saveAll(batch);
            preparedDataRepository.flush();
        }

        System.out.println("Préparation terminée.");
        System.out.println("Succès : " + success);
        System.out.println("Échecs ignorés : " + failed);
    }

    public List<String> getSecondResellersWithMissingInfo() {
        return preparedDataRepository.findDistinctSecondResellersWithMissingInfo();
    }

    @Transactional
    public int updatePreparedDataForReseller(String reseller, String secondReseller,
                                             String resellerTypeName, String channel) {
        List<PreparedData> preparedDataList = preparedDataRepository.findAll().stream()
                .filter(p -> p.getReseller() != null && p.getReseller().equals(reseller))
                .filter(p -> p.getSecondReseller() == null
                        || p.getSecondReseller().isEmpty()
                        || p.getSecondReseller().equalsIgnoreCase("(empty)"))
                .toList();

        for (PreparedData prepared : preparedDataList) {
            prepared.setSecondReseller(secondReseller);
            prepared.setResellerTypeName(resellerTypeName);
            prepared.setChannel(channel);
        }

        if (!preparedDataList.isEmpty()) {
            preparedDataRepository.saveAll(preparedDataList);
        }

        return preparedDataList.size();
    }

    public List<String> getEverythingMissing() {
        return preparedDataRepository.findDealEverythingMissing();
    }

    @Transactional
    public int updateResellerData(String reseller, String secondReseller,
                                  String resellerTypeName) {
        return preparedDataRepository.updateResellerFields(
                reseller, secondReseller, resellerTypeName
        );
    }

    private String cleanKey(String value) {
        if (value == null) return null;

        value = value.trim();

        if (value.isEmpty() || value.equalsIgnoreCase("(empty)")) {
            return null;
        }

        return value.toLowerCase();
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}