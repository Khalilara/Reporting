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
        int success = 0;
        int failed = 0;

        System.out.println("Suppression ancienne table sales_data...");
        repository.deleteAll();

        System.out.println("Suppression ancienne table prepared_data...");
        preparedDataRepository.deleteAll();

        System.out.println("Début insertion sales_data : " + dataList.size() + " lignes");

        for (int i = 0; i < dataList.size(); i++) {
            SalesData data = dataList.get(i);

            try {
                repository.save(data);
                success++;

                if (success % 500 == 0) {
                    repository.flush();
                    System.out.println("Lignes importées : " + success + " / " + dataList.size());
                }

            } catch (Exception e) {
                failed++;

                System.out.println("--------------------------------------");
                System.out.println("Ligne ignorée pendant l'insertion BDD : " + (i + 2));
                System.out.println("Erreur : " + e.getMessage());
                System.out.println("Reseller : " + data.getReseller());
                System.out.println("EndCustomer : " + data.getEndCustomer());
                System.out.println("--------------------------------------");
            }
        }

        repository.flush();

        System.out.println("Insertion terminée.");
        System.out.println("Succès : " + success);
        System.out.println("Échecs ignorés : " + failed);

        System.out.println("Début préparation des données...");
        dataPreparationService.prepareData();
        System.out.println("Préparation terminée.");
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