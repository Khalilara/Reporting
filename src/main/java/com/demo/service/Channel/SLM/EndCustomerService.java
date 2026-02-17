package com.demo.service.Channel.SLM;

import com.demo.Model.Channel.SLM.EndCustomer;
import com.demo.Repository.Channel.SLM.EndCustomerRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class EndCustomerService {

    public List<EndCustomer> importEndCustomerFromExcel(InputStream inputStream) throws Exception {
        List<EndCustomer> endCustomerList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            // Ignorer la première ligne (en-têtes)
            if (row.getRowNum() == 0) continue;

            EndCustomer endCustomer = new EndCustomer();

            // Mapping des colonnes
            // Col 0: endCustomerName
            String endCustomerName = getCellValue(row.getCell(0));
            // Col 1: licenceKey
            String licenceKey = getCellValue(row.getCell(1));

            // Valider que les données ne sont pas vides
            if (endCustomerName == null || endCustomerName.trim().isEmpty() ||
                licenceKey == null || licenceKey.trim().isEmpty()) {
                continue; // Ignorer les lignes incomplètes
            }

            endCustomer.setEndCustomerName(endCustomerName);
            endCustomer.setLicenceKey(licenceKey);

            endCustomerList.add(endCustomer);
        }

        workbook.close();
        return endCustomerList;
    }

    /**
     * Récupère la valeur d'une cellule en tant que String
     */
    private String getCellValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
