package com.demo.Controller.EBT;

import com.demo.Model.EBT.Status;
import com.demo.Model.EBT.StatusEvo;
import com.demo.Model.EBT.TableauEBT;
import com.demo.Model.EBT.EvolutionEBT;
import com.demo.Repository.EBT.EvolutionEBTRepository;
import com.demo.Repository.EBT.TableauEbtRepository;
import com.demo.service.EBT.EBTService;
import com.demo.service.EBT.EBTExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(
    origins = "http://localhost",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/ebt")
public class EBTController {
    @Autowired
    private EBTService ebtService;
    @Autowired
    private EBTExcelService ebtExcelService;
    @Autowired
    private TableauEbtRepository tableauEbtRepository;
    @Autowired
    private EvolutionEBTRepository evolutionEBTRepository;


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
    //////////////////////////////////////////////////::::::::///////////////////////////////////////Evolution Section///////////////////////////////////////////////////////////////////////////:::::::::::::::::::::::::::::::::::
     @GetMapping("/evolution/get_all")
    public ResponseEntity<List<EvolutionEBT>> getEvolutionEBT() {
        List<EvolutionEBT> data = evolutionEBTRepository.findAll();
        return ResponseEntity.ok(data);
    }
    @PostMapping("/evolution/create")
    public ResponseEntity<?> addEvolutionEBT(@RequestBody EvolutionEBT evolutionEBT) {
        try {
            evolutionEBTRepository.save(evolutionEBT);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la création de l'évolution EBT: "+e.getMessage());

        }
    }
    @PutMapping("/evolution/update/{id}")
    public ResponseEntity<?> updateEvolutionEBT(@PathVariable Long id, @RequestBody EvolutionEBT updatedEvolutionEBT) {
        try{
            EvolutionEBT existingEvolution = evolutionEBTRepository.findById(id)
                    .orElseThrow(()-> new RuntimeException("Evolution EBT non trouvée avec l'ID:" + id));

            existingEvolution.setGrossiste(updatedEvolutionEBT.getGrossiste());
            existingEvolution.setRevendeur(updatedEvolutionEBT.getRevendeur());
            existingEvolution.setClient(updatedEvolutionEBT.getClient());
            existingEvolution.setSolution(updatedEvolutionEBT.getSolution());
            existingEvolution.setClefDeLicence(updatedEvolutionEBT.getClefDeLicence());
            existingEvolution.setQuantite(updatedEvolutionEBT.getQuantite());
            existingEvolution.setPrix(updatedEvolutionEBT.getPrix());
            existingEvolution.setCaAttendu(updatedEvolutionEBT.getCaAttendu());
            existingEvolution.setCaVendu(updatedEvolutionEBT.getCaVendu());
            existingEvolution.setDateDeDebut(updatedEvolutionEBT.getDateDeDebut());
            existingEvolution.setDateDeFin(updatedEvolutionEBT.getDateDeFin());
            existingEvolution.setStatus(updatedEvolutionEBT.getStatus());
            existingEvolution.setProba(updatedEvolutionEBT.getProba());
            existingEvolution.setCommentaire(updatedEvolutionEBT.getCommentaire());
            existingEvolution.setQuarter(updatedEvolutionEBT.getQuarter());

            evolutionEBTRepository.save(existingEvolution);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Evolution EBT non trouvée avec l'ID:" + id+": "+e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur est survenue lors de la mise a jour:"+ e.getMessage());
        }
    }
    @PatchMapping("/evolution/patch-update/{id}")
    public ResponseEntity<?> partialUpdateEvolutionEBT(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            EvolutionEBT existingEvolution = evolutionEBTRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evolution EBT non trouvée avec l'ID: " + id));

            // Mise à jour dynamique des champs
            updates.forEach((key, value) -> {
                switch (key) {
                    case "grossiste":
                        existingEvolution.setGrossiste((String) value);
                        break;
                    case "revendeur":
                        existingEvolution.setRevendeur((String) value);
                        break;
                    case "client":
                        existingEvolution.setClient((String) value);
                        break;
                    case "solution":
                        existingEvolution.setSolution((String) value);
                        break;
                    case "clefDeLicence":
                        existingEvolution.setClefDeLicence((String) value);
                        break;
                    case "quantite":
                        existingEvolution.setQuantite(Double.parseDouble(value.toString()));
                        break;
                    case "prix":
                        existingEvolution.setPrix(Double.parseDouble(value.toString()));
                        break;
                    case "caAttendu":
                        existingEvolution.setCaAttendu(Double.parseDouble(value.toString()));
                        break;
                    case "caVendu":
                        existingEvolution.setCaVendu(Double.parseDouble(value.toString()));
                        break;
                    case "dateDeDebut":
                        existingEvolution.setDateDeDebut(java.time.LocalDate.parse(value.toString()));
                        break;
                    case "dateDeFin":
                        existingEvolution.setDateDeFin(java.time.LocalDate.parse(value.toString()));
                        break;
                    case "proba":
                        existingEvolution.setProba((String) value);
                        break;
                    case "commentaire":
                        existingEvolution.setCommentaire((String) value);
                        break;
                    case "quarter":
                        existingEvolution.setQuarter((String) value);
                        break;
                    case "status":
                        if (value == null) {
                            existingEvolution.setStatus(null);
                        } else {
                            try {
                                String statusValue = value.toString().trim();
                                if (statusValue.isEmpty()) {
                                    existingEvolution.setStatus(null);
                                } else {
                                    existingEvolution.setStatus(StatusEvo.valueOf(statusValue.toUpperCase()));
                                }
                            } catch (IllegalArgumentException e) {
                                throw new IllegalArgumentException("Statut invalide: " + value + ". Statuts valides: " +
                                        java.util.Arrays.toString(StatusEvo.values()));
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Champ inconnu: " + key);
                }
            });

            evolutionEBTRepository.save(existingEvolution);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour partielle: " + e.getMessage());
        }
    }
    @DeleteMapping("/evolution/delete/{id}")  
    public ResponseEntity<?> deleteEvolutionEBT(@PathVariable Long id) {
        try {
            if (!evolutionEBTRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Evolution EBT non trouvée avec l'ID: " + id);
            }
            evolutionEBTRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    // ========== UPLOAD EXCEL ENDPOINTS ==========
    @PostMapping("/upload/tableau")
    public ResponseEntity<?> uploadTableauEBTExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Le fichier est vide");
            }

            // Lire les données du fichier Excel
            List<TableauEBT> dataFromExcel = ebtExcelService.readTableauEBTFromExcel(file.getInputStream());

            if (dataFromExcel.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Aucune donnée valide trouvée dans le fichier");
            }

            // Supprimer toutes les données existantes
            tableauEbtRepository.deleteAll();

            // Insérer les nouvelles données
            List<TableauEBT> savedData = tableauEbtRepository.saveAll(dataFromExcel);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Fichier importé avec succès. " + savedData.size() + " enregistrements chargés.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload: " + e.getMessage());
        }
    }

    @PostMapping("/upload/evolution")
    public ResponseEntity<?> uploadEvolutionEBTExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier que le fichier n'est pas vide
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Le fichier est vide");
            }

            // Lire les données du fichier Excel
            List<EvolutionEBT> dataFromExcel = ebtExcelService.readEvolutionEBTFromExcel(file.getInputStream());

            if (dataFromExcel.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Aucune donnée valide trouvée dans le fichier");
            }

            // Supprimer toutes les données existantes
            evolutionEBTRepository.deleteAll();

            // Insérer les nouvelles données
            List<EvolutionEBT> savedData = evolutionEBTRepository.saveAll(dataFromExcel);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Fichier importé avec succès. " + savedData.size() + " enregistrements chargés.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload: " + e.getMessage());
        }
    }

}
