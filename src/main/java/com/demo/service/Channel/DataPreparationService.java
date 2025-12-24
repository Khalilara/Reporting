package com.demo.service.Channel;

import com.demo.Model.Channel.*;
import com.demo.Repository.Channel.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        preparedDataRepository.deleteAll();

        List<SalesData> salesDataList = salesDataRepository.findAll();
        List<PreparedData> preparedDataList = new ArrayList<>();

        Map<String, ResellerCateg> resellerMap = resellerCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ResellerCateg::getResellerName,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, CustumerCateg> customerMap = customerCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CustumerCateg::getName,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        Map<String, ProductCateg> productMap = productCategRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        ProductCateg::getProductSubSub,
                        Function.identity(),
                        (existing, replacement) -> existing
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

            // ✅ LOGIQUE CORRIGÉE pour Reseller
            // CAS 1: Si secondReseller existe et n'est pas empty
            if (sales.getSecondReseller() != null && 
                !sales.getSecondReseller().isEmpty() &&
                !sales.getSecondReseller().equalsIgnoreCase("(empty)")) {
                
                ResellerCateg reseller = resellerMap.get(sales.getSecondReseller());
                if (reseller != null) {
                    prepared.setResellerTypeName(reseller.getResellerTypeName());
                    prepared.setChannel(reseller.getChannel());
                }
            } 
            // CAS 2: Si secondReseller est empty, chercher dans la lookup table
            else if (sales.getReseller() != null) {
                // ✅ Chercher d'abord dans ResellerWithOut2ndReseller
                Optional<ResellerWithOut2ndReseller> mapping = 
                    resellerWithOut2ndResellerRepository.findByReseller(sales.getReseller());
                
                if (mapping.isPresent()) {
                    // Utiliser le mapping de la lookup table
                    prepared.setSecondReseller(mapping.get().getSecondReseller());
                    prepared.setResellerTypeName(mapping.get().getResellerTypeName());
                    prepared.setChannel(mapping.get().getChannel());
                } else {
                    // Fallback: chercher avec reseller dans ResellerCateg
                    ResellerCateg reseller = resellerMap.get(sales.getReseller());
                    if (reseller != null) {
                        prepared.setResellerTypeName(reseller.getResellerTypeName());
                        prepared.setChannel(reseller.getChannel());
                    }
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
    
    /**
     * Met à jour instantanément les données PreparedData pour un reseller spécifique
     * Appelée après la création d'un mapping dans ResellerWithOut2ndReseller
     */
    @Transactional
    public int updatePreparedDataForReseller(String reseller, String secondReseller, 
                                             String resellerTypeName, String channel) {
        // Récupérer tous les PreparedData pour ce reseller avec secondReseller vide
        List<PreparedData> preparedDataList = preparedDataRepository.findAll().stream()
            .filter(p -> p.getReseller() != null && p.getReseller().equals(reseller))
            .filter(p -> p.getSecondReseller() == null || p.getSecondReseller().isEmpty() 
                      || p.getSecondReseller().equalsIgnoreCase("(empty)"))
            .toList();
        
        // Mettre à jour chaque enregistrement
        for (PreparedData prepared : preparedDataList) {
            prepared.setSecondReseller(secondReseller);
            prepared.setResellerTypeName(resellerTypeName);
            prepared.setChannel(channel);
        }
        
        // Sauvegarder les modifications
        if (!preparedDataList.isEmpty()) {
            preparedDataRepository.saveAll(preparedDataList);
        }
        
        return preparedDataList.size();
    }
    
    // ✅ AJOUTER CETTE MÉTHODE
    public List<String> getEverythingMissing() {
        return preparedDataRepository.findDealEverythingMissing();
    }
    
    @Transactional
    public int updateResellerData(String reseller, String secondReseller, 
                                  String resellerTypeName) {
        return preparedDataRepository.updateResellerFields(
            reseller, secondReseller, resellerTypeName);
    }
}