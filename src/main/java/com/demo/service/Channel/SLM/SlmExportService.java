package com.demo.service.Channel.SLM;

import com.demo.Model.Channel.SLM.SLM;
import com.demo.Repository.Channel.SLM.SlmRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SlmExportService {

    private final SlmRepository slmRepository;

    public SlmExportService(SlmRepository slmRepository) {
        this.slmRepository = slmRepository;
    }

    public byte[] exportMissingClientTypeToExcel() throws Exception {
        // Récupère TOUS les SLM où clientType est NULL ou vide
        List<SLM> slmList = slmRepository.findAllWithMissingClientType();

        // Création du workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("SLM_Missing_ClientType");

        // Ligne d'en-tête
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("1st Channel");
        header.createCell(1).setCellValue("2nd Channel");
        header.createCell(2).setCellValue("End Customer");

        // Données (3 premières colonnes uniquement)
        int rowIdx = 1;
        for (SLM slm : slmList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(slm.getFirstChannel() != null ? slm.getFirstChannel() : "");
            row.createCell(1).setCellValue(slm.getSecondChannel() != null ? slm.getSecondChannel() : "");
            row.createCell(2).setCellValue(slm.getEndCustomer() != null ? slm.getEndCustomer() : "");
        }

        // Ajuste la largeur des colonnes automatiquement
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convertit en byte array pour le retour HTTP
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
