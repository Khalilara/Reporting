package com.demo.service.EBT;

import com.demo.Model.EBT.Status;
import com.demo.Model.EBT.TableauEBT;
import com.demo.Repository.EBT.TableauEbtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EbtDashboardService {

    @Autowired
    TableauEbtRepository repository;


    public ResponseEntity<Map<String, Map<String, Double>>> getCA() {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (TableauEBT elmt : repository.findAll()) {
            // Ne prendre en compte que les statuts INVOICED
            if (elmt.getStatus() != Status.INVOICED) {
                continue;
            }

            String quarter = elmt.getQuarter();
            String type = elmt.getType();
            Double ca = elmt.getChiffreAffaire();

            result.computeIfAbsent(quarter, q -> new HashMap<>());
            result.get(quarter).merge(type, ca, Double::sum);
        }

        return ResponseEntity.ok(result);
    }



    public ResponseEntity<Map<String, Map<String, Double>>> getProba() {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (TableauEBT elmt : repository.findAll()) {
            String quarter = elmt.getQuarter(); // ex: "Q1"
            String proba = elmt.getProbabilite(); // ex: "90%"
            Double ca = elmt.getChiffreAffaire(); // ex: 10000.0

            // Initialise le quarter s'il n'existe pas encore
            result.computeIfAbsent(quarter, q -> new HashMap<>());

            // Ajoute ou additionne le CA à la bonne proba dans le bon quarter
            result.get(quarter).merge(proba, ca, Double::sum);
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Map<String, Map<String, Double>>> getProbaCumuler() {
        Map<String, Map<String, Double>> result = new HashMap<>();

        // Étape 1 : CA facturé (statut INVOICED)
        Map<String, Map<String, Double>> caFactureParQuarterEtType = getCA().getBody();
        Map<String, Double> caFactureTotalParQuarter = new HashMap<>();

        if (caFactureParQuarterEtType != null) {
            for (Map.Entry<String, Map<String, Double>> entry : caFactureParQuarterEtType.entrySet()) {
                String quarter = entry.getKey();
                double total = entry.getValue().values().stream().mapToDouble(Double::doubleValue).sum();
                caFactureTotalParQuarter.put(quarter, total);
            }
        }

        // Étape 2 : CA des deals non facturés (hors INVOICED), groupés par quarter et proba (A, B, C)
        Map<String, Map<String, Double>> caParQuarterEtProba = new HashMap<>();

        for (TableauEBT elmt : repository.findAll()) {
            if (elmt.getStatus() == Status.INVOICED) continue;

            String quarter = elmt.getQuarter();
            String proba = elmt.getProbabilite(); // attendu : "A", "B", ou "C"
            Double ca = elmt.getChiffreAffaire();

            caParQuarterEtProba.computeIfAbsent(quarter, q -> new HashMap<>());
            caParQuarterEtProba.get(quarter).merge(proba, ca, Double::sum);
        }

        // Étape 3 : construction du résultat final par quarter
        for (String quarter : caParQuarterEtProba.keySet()) {
            Map<String, Double> probaMap = caParQuarterEtProba.get(quarter);
            Map<String, Double> cumulMap = new HashMap<>();

            double caInvoiced = caFactureTotalParQuarter.getOrDefault(quarter, 0.0);

            // Pour A et B : ajouter au CA INVOICED
            if (probaMap.containsKey("A")) {
                cumulMap.put("A", caInvoiced + probaMap.get("A"));
            }
            if (probaMap.containsKey("B")) {
                cumulMap.put("B", caInvoiced + probaMap.get("B"));
            }
            // Pour C : seulement la valeur brute
            if (probaMap.containsKey("C")) {
                cumulMap.put("C", probaMap.get("C"));
            }

            result.put(quarter, cumulMap);
        }

        return ResponseEntity.ok(result);
    }

}
