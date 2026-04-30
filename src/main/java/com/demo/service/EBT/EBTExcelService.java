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
import java.util.ArrayList;
import java.util.List;

@Service
public class EBTExcelService {

    public List<TableauEBT> readTableauEBTFromExcel(InputStream inputStream) throws Exception {
        List<TableauEBT> data = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                TableauEBT tableau = new TableauEBT();
                
                try {
                    tableau.setType(getCellValueAsString(row, 0));
                    tableau.setProbabilite(getCellValueAsString(row, 1));
                    tableau.setChiffreAffaire(getCellValueAsDouble(row, 2));
                    tableau.setClient(getCellValueAsString(row, 3));
                    tableau.setSolution(getCellValueAsString(row, 4));
                    tableau.setQuantite(getCellValueAsDouble(row, 5));
                    tableau.setPrix(getCellValueAsDouble(row, 6));
                    tableau.setKam(getCellValueAsString(row, 7));
                    tableau.setInfo(getCellValueAsString(row, 8));
                    tableau.setQuarter(getCellValueAsString(row, 9));
                    
                    String statusStr = getCellValueAsString(row, 10);
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

    public List<EvolutionEBT> readEvolutionEBTFromExcel(InputStream inputStream) throws Exception {
        List<EvolutionEBT> data = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                EvolutionEBT evolution = new EvolutionEBT();
                
                try {
                    evolution.setGrossiste(getCellValueAsString(row, 0));
                    evolution.setRevendeur(getCellValueAsString(row, 1));
                    evolution.setClient(getCellValueAsString(row, 2));
                    evolution.setSolution(getCellValueAsString(row, 3));
                    evolution.setClefDeLicence(getCellValueAsString(row, 4));
                    evolution.setQuantite(getCellValueAsDouble(row, 5));
                    evolution.setPrix(getCellValueAsDouble(row, 6));
                    evolution.setCaAttendu(getCellValueAsDouble(row, 7));
                    evolution.setCaVendu(getCellValueAsDouble(row, 8));
                    evolution.setDateDeDebut(getCellValueAsLocalDate(row, 9));
                    evolution.setDateDeFin(getCellValueAsLocalDate(row, 10));
                    evolution.setProba(getCellValueAsString(row, 11));
                    evolution.setCommentaire(getCellValueAsString(row, 12));
                    evolution.setQuarter(getCellValueAsString(row, 13));
                    
                    String statusStr = getCellValueAsString(row, 14);
                    if (statusStr != null && !statusStr.isEmpty()) {
                        try {
                            evolution.setStatus(StatusEvo.valueOf(statusStr.toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            evolution.setStatus(null);
                        }
                    }
                    
                    data.add(evolution);
                } catch (Exception e) {
                    // Skip rows with parsing errors
                    continue;
                }
            }
        }
        
        return data;
    }

    private String getCellValueAsString(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    private Double getCellValueAsDouble(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return 0.0;
        
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        return 0.0;
    }

    private LocalDate getCellValueAsLocalDate(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return LocalDate.now();
        
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return LocalDate.now();
    }
}
