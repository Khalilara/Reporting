
package com.demo.service.Channel.SLM;

import com.demo.Model.Channel.SLM.SLM;
import com.demo.Model.Channel.SLM.SLMExcel;
import com.demo.Model.Channel.SLM.Status;
import com.demo.Model.Channel.SLM.EndCustomer;
import com.demo.Model.Channel.CustumerCateg;
import com.demo.Model.Channel.SLM.Product;
import com.demo.Repository.Channel.SLM.ProductRepository;
import com.demo.Repository.Channel.CustomerCategRepository;
import com.demo.Repository.Channel.SLM.EndCustomerRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;  
import java.util.Optional;
import java.time.LocalDate;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Cell;

@Service
public class SlmService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerCategRepository customerCategRepository;

    @Autowired
    private EndCustomerRepository endCustomerRepository;

    /**
     * Lit le fichier Excel et retourne une liste de SLMExcel
     * (objets qui mappent exactement les colonnes du fichier)
     */
    public List<SLMExcel> readExcelFile(InputStream inputStream) throws Exception {
        List<SLMExcel> dataList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            // Ignorer les 2 premières lignes (No + en-têtes)
            if (row.getRowNum() <= 1) continue;

            SLMExcel excelData = new SLMExcel();

            // Mapping des colonnes Excel
            // Col 0: No (ignoré)
            // Col 1: Division
            excelData.setDivision(getCellValue(row.getCell(1)));
            // Col 2: 1st Channel
            excelData.setFirstChannel(getCellValue(row.getCell(2)));
            // Col 3: 2nd Channel
            excelData.setSecondChannel(getCellValue(row.getCell(3)));
            // Col 4: License Key
            excelData.setLicenceKey(getCellValue(row.getCell(4)));
            // Col 5: Model Name
            excelData.setModelName(getCellValue(row.getCell(5)));
            // Col 6: Quantit
            excelData.setQuantiter(getNumericValue(row.getCell(6)));
            // Col 7: Price Type (ignoré après)
            excelData.setPriceType(getCellValue(row.getCell(7)));
            // Col 8: Product Type (ignoré après)
            excelData.setProductType(getCellValue(row.getCell(8)));
            // Col 9: Type (CRUCIAL - filtrer les "Trial")
            excelData.setType(getCellValue(row.getCell(9)));
            // Col 10: License Status
            excelData.setLicenceStatus(getCellValue(row.getCell(10)));
            // Col 11: SO No. (ignoré après)
            excelData.setSoNo(getCellValue(row.getCell(11)));
            // Col 12: MDM Name (ignoré après)
            excelData.setMdmName(getCellValue(row.getCell(12)));
            // Col 13: [rated](Requested) (ignoré après)
            excelData.setRatedRequested(getCellValue(row.getCell(13)));
            // Col 14: End Date
            excelData.setEndDate(getCellDateValue(row.getCell(14)));

            dataList.add(excelData);
        }

        workbook.close();
        return dataList;
    }

    /**
     * Convertit une liste de SLMExcel en liste de SLM
     * - Filtre les enregistrements avec Type = "Trial"
     * - Récupère le prix unitaire via ProductRepository
     * - Calcule le totalPrice (quantiter * prixUnitaire)
     * - Récupère le clientType via CustomerCategRepository (à faire après)
     */
    public List<SLM> convertirExcelToSLM(List<SLMExcel> excelDataList) {
        List<SLM> slmList = new ArrayList<>();

        for (SLMExcel excelData : excelDataList) {
            // FILTRAGE: Ignorer tous les enregistrements avec Type = "Trial"
            if ("Trial".equalsIgnoreCase(excelData.getType())) {
                continue;
            }

            SLM slm = new SLM();

            // Copier les données directes
            slm.setFirstChannel(excelData.getFirstChannel());
            slm.setSecondChannel(excelData.getSecondChannel());
            slm.setLicenceKey(excelData.getLicenceKey());
            slm.setModelName(excelData.getModelName());
            slm.setQuantiter(excelData.getQuantiter());

            // Convertir le License Status (String → Enum Status)
            try {
                slm.setLicenceStatus(Status.valueOf(excelData.getLicenceStatus()));
            } catch (Exception e) {
                slm.setLicenceStatus(Status.Active); // Valeur par défaut
            }

            slm.setEndDate(excelData.getEndDate());

            // JOINTURE 1: Chercher le prix unitaire via Product
            Optional<Product> product = productRepository.findByModelName(excelData.getModelName());
            if (product.isPresent()) {
                slm.setPrixUnitaire(product.get().getPrixUnitaire());
                // Calculer le totalPrice
                slm.setTotalPrice(excelData.getQuantiter() * product.get().getPrixUnitaire());
            } else {
                // Si le produit n'existe pas, laisser les prix à 0
                slm.setPrixUnitaire(0);
                slm.setTotalPrice(0);
            }

            // JOINTURE 2: Chercher le endCustomer via licenceKey
            Optional<EndCustomer> endCustomer = endCustomerRepository.findByLicenceKey(excelData.getLicenceKey());
            if (endCustomer.isPresent()) {
                slm.setEndCustomer(endCustomer.get().getEndCustomerName());
            } else {
                slm.setEndCustomer(""); // Laisser vide si pas de correspondance
            }

            // JOINTURE 3: Chercher le clientType via CustomerCateg (par endCustomer)
            // Chercher le clientType en comparant CustomerCateg.name avec SLM.endCustomer
            if (slm.getEndCustomer() != null && !slm.getEndCustomer().isEmpty()) {
                List<CustumerCateg> custCategList = customerCategRepository.findAll();
                CustumerCateg matchedCustCateg = null;
                for (CustumerCateg cc : custCategList) {
                    if (cc.getName().equalsIgnoreCase(slm.getEndCustomer())) {
                        matchedCustCateg = cc;
                        break;
                    }
                }
                if (matchedCustCateg != null) {
                    slm.setClientType(matchedCustCateg.getCategory());
                } else {
                    slm.setClientType(""); // Laisser vide si pas de correspondance
                }
            } else {
                slm.setClientType("");
            }

            // Les autres attributs (title) seront remplis par d'autres jointures
            // TODO: À configurer après

            slmList.add(slm);
        }

        return slmList;
    }

    /**
     * Méthode complète : lit le Excel et convertit directement en SLM
     */
    public List<SLM> importSLMFromExcel(InputStream inputStream) throws Exception {
        List<SLMExcel> excelData = readExcelFile(inputStream);
        return convertirExcelToSLM(excelData);
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

    /**
     * Récupère la valeur numérique d'une cellule
     */
    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    /**
     * Récupère une date d'une cellule
     */
    private LocalDate getCellDateValue(Cell cell) {
        if (cell == null) return null;
        
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getLocalDateTimeCellValue().toLocalDate();
                    }
                    break;
                case STRING:
                    // Formats Excel courants : "12/29/2025", "03/28/2026"
                    String dateStr = cell.getStringCellValue().trim();
                    if (!dateStr.isEmpty()) {
                        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                    }
                    break;
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}