package com.demo.service.EBT;

import com.demo.Model.EBT.TableauEBT;
import com.demo.Model.EBT.EvolutionEBT;
import com.demo.Model.EBT.Status;
import com.demo.Model.EBT.StatusEvo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EBTExcelService {

    public List<TableauEBT> readTableauEBTFromExcel(InputStream inputStream, boolean forceIndex) throws Exception {
        List<TableauEBT> data = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.FRANCE);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Map<String, Integer> headerIndex = buildHeaderIndexMap(sheet, formatter, evaluator);

            int typeIdx = resolveColumnIndex(headerIndex, "type");
            int probaIdx = resolveColumnIndex(headerIndex, "pb", "probabilite", "proba");
            int clientIdx = resolveColumnIndex(headerIndex, "client");
            int solutionIdx = resolveColumnIndex(headerIndex, "solution");
            int qteIdx = resolveColumnIndex(headerIndex, "qte", "quantite");
            int prixIdx = resolveColumnIndex(headerIndex, "prix");
            int caIdx = resolveColumnIndex(headerIndex, "ca", "ca$", "caht", "chiffreaffaire", "chiffredaffaire", "chiffreaffaires");
            int kamIdx = resolveColumnIndex(headerIndex, "kam");
            int infoIdx = resolveColumnIndex(headerIndex, "lastupdate", "lastupdateinformations", "lastupdateinformation", "informations", "information", "info", "commentaire");
            int quarterIdx = resolveColumnIndex(headerIndex, "quarter", "trimestre");
            int statusIdx = resolveColumnIndex(headerIndex, "status");

            // If forceIndex requested, always use legacy positional mapping
            if (forceIndex) {
                typeIdx = 0;
                probaIdx = 1;
                caIdx = 2;
                clientIdx = 3;
                solutionIdx = 4;
                qteIdx = 5;
                prixIdx = 6;
                kamIdx = 7;
                infoIdx = 8;
                quarterIdx = 9;
                statusIdx = 10;
            } else {
                // Fallback ancien format si la feuille ne contient pas d'en-têtes reconnus
                if (typeIdx == -1 && probaIdx == -1 && clientIdx == -1 && solutionIdx == -1) {
                    typeIdx = 0;
                    probaIdx = 1;
                    caIdx = 2;
                    clientIdx = 3;
                    solutionIdx = 4;
                    qteIdx = 5;
                    prixIdx = 6;
                    kamIdx = 7;
                    infoIdx = 8;
                    quarterIdx = 9;
                    statusIdx = 10;
                }
            }
            
            System.out.println("DEBUG: resolved qteIdx=" + qteIdx + " for sheet 'TableauEBT'");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                TableauEBT tableau = new TableauEBT();
                
                try {
                    tableau.setType(getCellValueAsString(row, typeIdx, formatter, evaluator));
                    tableau.setProbabilite(getCellValueAsString(row, probaIdx, formatter, evaluator));
                    tableau.setClient(getCellValueAsString(row, clientIdx, formatter, evaluator));
                    tableau.setSolution(getCellValueAsString(row, solutionIdx, formatter, evaluator));
                    // Debug: show raw value at the expected quantite column index
                    try {
                        String rawQte = "";
                        Cell qCell = (qteIdx >= 0) ? row.getCell(qteIdx) : null;
                        if (qCell != null) rawQte = formatter.formatCellValue(qCell, evaluator);
                        System.out.println("DEBUG: row=" + i + " qteIdx=" + qteIdx + " rawQte='" + rawQte + "' cellType=" + (qCell==null?"null":qCell.getCellType()));
                    } catch (Exception e) {
                        System.out.println("DEBUG: unable to read raw qte for row=" + i + " idx=" + qteIdx);
                    }
                    tableau.setQuantite(getCellValueAsDouble(row, qteIdx, formatter, evaluator));
                    tableau.setPrix(getCellValueAsDouble(row, prixIdx, formatter, evaluator));
                    tableau.setChiffreAffaire(getCellValueAsDouble(row, caIdx, formatter, evaluator));
                    tableau.setKam(getCellValueAsString(row, kamIdx, formatter, evaluator));
                    tableau.setInfo(getCellValueAsString(row, infoIdx, formatter, evaluator));
                    tableau.setQuarter(getCellValueAsString(row, quarterIdx, formatter, evaluator));

                    String statusStr = getCellValueAsString(row, statusIdx, formatter, evaluator);
                    if (statusStr != null && !statusStr.isEmpty()) {
                        try {
                            tableau.setStatus(Status.valueOf(statusStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            tableau.setStatus(null);
                        }
                    }
                    
                    data.add(tableau);
                } catch (Exception e) {
                    // Skip rows with parsing errors
                    continue;
                }
            }
        }
        
        return data;
    }

    public List<EvolutionEBT> readEvolutionEBTFromExcel(InputStream inputStream, boolean forceIndex) throws Exception {
        List<EvolutionEBT> data = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.FRANCE);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Map<String, Integer> headerIndex = buildHeaderIndexMap(sheet, formatter, evaluator);

            int grossisteIdx = resolveColumnIndex(headerIndex, "grossiste");
            int revendeurIdx = resolveColumnIndex(headerIndex, "revendeur");
            int clientIdx = resolveColumnIndex(headerIndex, "client");
            int solutionIdx = resolveColumnIndex(headerIndex, "solution");
            int cleIdx = resolveColumnIndex(headerIndex, "cledelicence", "clede licence", "cledeslicence", "clefdelicence", "clefde licence");
            int qteIdx = resolveColumnIndex(headerIndex, "qte", "qtes", "quantite");
            int prixIdx = resolveColumnIndex(headerIndex, "prix");
            int caAttenduIdx = resolveColumnIndex(headerIndex, "caattendu", "caattendu$", "caattendu ", "caattendu€");
            int caVenduIdx = resolveColumnIndex(headerIndex, "cavendu", "cavendu$", "cavendu ", "cavendu€");
            int dateDebutIdx = resolveColumnIndex(headerIndex, "datededebut", "datedebut", "datedebut ", "datededebut/");
            int dateFinIdx = resolveColumnIndex(headerIndex, "datefin", "datefin ", "datefin/", "datedefin");
            int statusIdx = resolveColumnIndex(headerIndex, "statust", "status", "statut");
            int probaIdx = resolveColumnIndex(headerIndex, "prob", "proba", "probabilite");
            int commIdx = resolveColumnIndex(headerIndex, "comm", "commentaire", "comment", "lastupdateinformations", "lastupdateinformation", "lastupdate", "informations");
            int quarterIdx = resolveColumnIndex(headerIndex, "quarter", "trimestre");

            // If forceIndex requested, always use legacy positional mapping
            if (forceIndex) {
                grossisteIdx = 0;
                revendeurIdx = 1;
                clientIdx = 2;
                solutionIdx = 3;
                cleIdx = 4;
                qteIdx = 5;
                prixIdx = 6;
                caAttenduIdx = 7;
                caVenduIdx = 8;
                dateDebutIdx = 9;
                dateFinIdx = 10;
                statusIdx = 11;
                probaIdx = 12;
                commIdx = 13;
            } else {
                // Fallback ancien format si aucun en-tête exploitable n'est trouvé
                if (grossisteIdx == -1 && clientIdx == -1 && solutionIdx == -1 && prixIdx == -1) {
                    grossisteIdx = 0;
                    revendeurIdx = 1;
                    clientIdx = 2;
                    solutionIdx = 3;
                    cleIdx = 4;
                    qteIdx = 5;
                    prixIdx = 6;
                    caAttenduIdx = 7;
                    caVenduIdx = 8;
                    dateDebutIdx = 9;
                    dateFinIdx = 10;
                    statusIdx = 11;
                    probaIdx = 12;
                    commIdx = 13;
                }
            }
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                EvolutionEBT evolution = new EvolutionEBT();
                
                try {
                    evolution.setGrossiste(getCellValueAsString(row, grossisteIdx, formatter, evaluator));
                    evolution.setRevendeur(getCellValueAsString(row, revendeurIdx, formatter, evaluator));
                    evolution.setClient(getCellValueAsString(row, clientIdx, formatter, evaluator));
                    evolution.setSolution(getCellValueAsString(row, solutionIdx, formatter, evaluator));
                    evolution.setClefDeLicence(getCellValueAsString(row, cleIdx, formatter, evaluator));
                    evolution.setQuantite(getCellValueAsDouble(row, qteIdx, formatter, evaluator));
                    evolution.setPrix(getCellValueAsDouble(row, prixIdx, formatter, evaluator));
                    evolution.setCaAttendu(getCellValueAsDouble(row, caAttenduIdx, formatter, evaluator));
                    evolution.setCaVendu(getCellValueAsDouble(row, caVenduIdx, formatter, evaluator));
                    evolution.setDateDeDebut(getCellValueAsLocalDate(row, dateDebutIdx, formatter, evaluator));
                    evolution.setDateDeFin(getCellValueAsLocalDate(row, dateFinIdx, formatter, evaluator));
                    evolution.setStatus(parseEvolutionStatus(getCellValueAsString(row, statusIdx, formatter, evaluator)));
                    evolution.setProba(getCellValueAsString(row, probaIdx, formatter, evaluator));
                    evolution.setCommentaire(getCellValueAsString(row, commIdx, formatter, evaluator));
                    evolution.setQuarter(getCellValueAsString(row, quarterIdx, formatter, evaluator));
                    
                    data.add(evolution);
                } catch (Exception e) {
                    // Skip rows with parsing errors
                    continue;
                }
            }
        }
        
        return data;
    }

    private Map<String, Integer> buildHeaderIndexMap(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        Map<String, Integer> indexMap = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return indexMap;
        }

        for (Cell cell : headerRow) {
            String header = normalizeHeader(formatter.formatCellValue(cell, evaluator));
            if (!header.isEmpty() && !indexMap.containsKey(header)) {
                indexMap.put(header, cell.getColumnIndex());
            }
        }

        return indexMap;
    }

    private int resolveColumnIndex(Map<String, Integer> headerIndex, String... aliases) {
        for (String alias : aliases) {
            Integer idx = headerIndex.get(normalizeHeader(alias));
            if (idx != null) {
                return idx;
            }
        }
        return -1;
    }

    private String getCellValueAsString(Row row, int columnIndex, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (columnIndex < 0) return "";
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private String getCellValueAsString(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (numericValue == Math.rint(numericValue)) {
                return String.valueOf((long) numericValue);
            }
            return String.valueOf(numericValue).trim();
        }
        return cell.toString().trim();
    }

    private Double getCellValueAsDouble(Row row, int columnIndex, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (columnIndex < 0) return 0.0;
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return 0.0;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        String raw = formatter.formatCellValue(cell, evaluator).trim();
        if (raw.isEmpty()) return 0.0;

        String normalized = raw
                .replace("\u00A0", "")
                .replace("\u202F", "")
                .replace(" ", "")
                .replace("\u2009", "")
                .replace(",", ".");

        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private Double getCellValueAsDouble(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return 0.0;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        String raw = getCellValueAsString(row, columnIndex);
        if (raw.isEmpty()) return 0.0;

        String normalized = raw
                .replace("\u00A0", "")
                .replace("\u202F", "")
                .replace(" ", "")
                .replace("\u2009", "")
                .replace(",", ".");

        try {
            return Double.parseDouble(normalized);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private LocalDate getCellValueAsLocalDate(Row row, int columnIndex, DataFormatter formatter, FormulaEvaluator evaluator) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return LocalDate.now();
        
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String raw = formatter.formatCellValue(cell, evaluator).trim();
        if (raw.isEmpty()) return LocalDate.now();

        String[] patterns = {"dd/MM/yyyy", "d/M/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy"};
        for (String pattern : patterns) {
            try {
                return java.time.LocalDate.parse(raw, java.time.format.DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {
            }
        }

        return LocalDate.now();
    }

    private StatusEvo parseEvolutionStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        try {
            return StatusEvo.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }

        // Remove diacritics (é, è, î, etc.) so headers like "quantité" match "quantite"
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized
            .trim()
            .toLowerCase(Locale.ROOT)
            .replace("\u00A0", "")
            .replace("\u202F", "")
            .replaceAll("[^a-z0-9]+", "");
    }
}
