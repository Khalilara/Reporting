package com.demo.Controller.EBT;

import com.demo.Model.EBT.Status;
import com.demo.Model.EBT.TableauEBT;
import com.demo.Repository.EBT.TableauEbtRepository;
import com.demo.service.EBT.EBTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ebt")
public class EBTController {
    @Autowired
    private EBTService ebtService;
    @Autowired
    private TableauEbtRepository tableauEbtRepository;

    @GetMapping("/get_all")
    public ResponseEntity<List<TableauEBT>> getEBT() {
        List<TableauEBT> data = tableauEbtRepository.findAll();
        return ResponseEntity.ok(data);
    }
    @PostMapping("/create")
    public ResponseEntity<?> addEbtTableau(@RequestBody TableauEBT tableauEBT) {
        try {
            tableauEbtRepository.save(tableauEBT);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création du tableau: "+e.getMessage());

        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEbtTableau(@PathVariable Long id, @RequestBody TableauEBT updatedTableauEBT) {
        try{
            TableauEBT existingTableau = tableauEbtRepository.findById(id)
                    .orElseThrow(()-> new RuntimeException("Tableau ET non trouve avec l'ID:" + id));

            existingTableau.setType(updatedTableauEBT.getType());
            existingTableau.setProbabilite(updatedTableauEBT.getProbabilite());
            existingTableau.setChiffreAffaire(updatedTableauEBT.getChiffreAffaire());
            existingTableau.setStatus(updatedTableauEBT.getStatus());
            existingTableau.setClient(updatedTableauEBT.getClient());
            existingTableau.setSolution(updatedTableauEBT.getSolution());
            existingTableau.setQuantite(updatedTableauEBT.getQuantite());
            existingTableau.setPrix(updatedTableauEBT.getPrix());
            existingTableau.setKam(updatedTableauEBT.getKam());
            existingTableau.setInfo(updatedTableauEBT.getInfo());
            existingTableau.setQuarter(updatedTableauEBT.getQuarter());

            tableauEbtRepository.save(existingTableau);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tableau ET non trouve avec l'ID:" + id+": "+e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la mise a jour:"+ e.getMessage());
        }
    }
    @PatchMapping("/patch-update/{id}")
    public ResponseEntity<?> partialUpdateEbtTableau(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            TableauEBT existingTableau = tableauEbtRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tableau EBT non trouvé avec l'ID: " + id));

            // Mise à jour dynamique des champs
            updates.forEach((key, value) -> {
                switch (key) {
                    case "type":
                        existingTableau.setType((String) value);
                        break;
                    case "chiffreAffaire":
                        existingTableau.setChiffreAffaire(Double.parseDouble(value.toString()));
                        break;
                    case "probabilite":
                        existingTableau.setProbabilite((String) value);
                        break;
                    case "client":
                        existingTableau.setClient((String) value);
                        break;
                    case "solution":
                        existingTableau.setSolution((String) value);
                        break;
                    case "quantite":
                        existingTableau.setQuantite(Double.parseDouble(value.toString()));
                        break;
                    case "prix":
                        existingTableau.setPrix(Double.parseDouble(value.toString()));
                        break;
                    case "kam":
                        existingTableau.setKam((String) value);
                        break;
                    case "info":
                        existingTableau.setInfo((String) value);
                        break;
                    case "quarter":
                        existingTableau.setQuarter((String) value);
                        break;
                    case "status": // Unifié sur "status" seulement
                        if (value == null) {
                            existingTableau.setStatus(null);
                        } else {
                            try {
                                String statusValue = value.toString().trim();
                                if (statusValue.isEmpty()) {
                                    existingTableau.setStatus(null);
                                } else {
                                    existingTableau.setStatus(Status.valueOf(statusValue.toUpperCase()));
                                }
                            } catch (IllegalArgumentException e) {
                                throw new IllegalArgumentException("Statut invalide: " + value + ". Statuts valides: " +
                                        java.util.Arrays.toString(Status.values()));
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Champ inconnu: " + key);
                }
            });

            tableauEbtRepository.save(existingTableau);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour partielle: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")  // Correction: ajout de /{id}
    public ResponseEntity<?> deleteEbtTableau(@PathVariable Long id) {
        try {
            if (!tableauEbtRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tableau EBT non trouvé avec l'ID: " + id);
            }
            tableauEbtRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

}
