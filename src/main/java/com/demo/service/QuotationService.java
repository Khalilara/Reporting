package com.demo.service;

import com.demo.Model.Quotation;
import com.demo.dto.QuotationDTO;
import com.demo.Repository.QuotationRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class QuotationService {
    
    private static final Logger logger = Logger.getLogger(QuotationService.class.getName());
    
    @Autowired
    private QuotationRepository quotationRepository;
    
    public Page<Quotation> getAllQuotations(Pageable pageable) {
        return quotationRepository.findAll(pageable);
    }
    
    public Quotation getQuotationById(Long id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation not found: " + id));
    }
    
    public Quotation createQuotation(QuotationDTO dto) {
        Quotation quotation = new Quotation();
        mapDtoToEntity(dto, quotation);
        return quotationRepository.save(quotation);
    }
    
    public Quotation updateQuotation(Long id, QuotationDTO dto) {
        Quotation quotation = getQuotationById(id);
        mapDtoToEntity(dto, quotation);
        return quotationRepository.save(quotation);
    }
    
    public void deleteQuotation(Long id) {
        quotationRepository.deleteById(id);
    }
    
    public List<Quotation> uploadQuotationsFromExcel(MultipartFile file) throws IOException {
        List<Quotation> quotations = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Quotation quotation = new Quotation();
                    
                    quotation.setAnnee(getIntegerValue(row.getCell(0)));
                    quotation.setMois(getStringValue(row.getCell(1)));
                    quotation.setDateEmissionsDevis(getDateValue(row.getCell(2)));
                    quotation.setStatusProjet(getStringValue(row.getCell(3)));
                    quotation.setGrossisteOperateur(getStringValue(row.getCell(4)));
                    quotation.setRevendeur(getStringValue(row.getCell(5)));
                    quotation.setClientFinal(getStringValue(row.getCell(6)));
                    quotation.setNomSolution(getStringValue(row.getCell(7)));
                    quotation.setRefSolution(getStringValue(row.getCell(8)));
                    quotation.setQuantiter(getIntegerValue(row.getCell(9)));
                    quotation.setDureeAnnee(getIntegerValue(row.getCell(10)));
                    quotation.setPaGrossiste(getBigDecimalValue(row.getCell(11)));
                    quotation.setTotalCAPotentiel(getBigDecimalValue(row.getCell(12)));
                    quotation.setDateFinValiditeCotation(getDateValue(row.getCell(13)));
                    quotation.setContactKnox(getStringValue(row.getCell(14)));
                    quotation.setKamEndCustomer(getStringValue(row.getCell(15)));
                    quotation.setRdvClient(getDateValue(row.getCell(16)));
                    quotation.setCommentaires(getStringValue(row.getCell(17)));
                    
                    quotations.add(quotation);
                } catch (Exception e) {
                    logger.warning("Erreur lors du traitement de la ligne " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        
        return quotationRepository.saveAll(quotations);
    }
    
    private void mapDtoToEntity(QuotationDTO dto, Quotation entity) {
        entity.setAnnee(dto.getAnnee());
        entity.setMois(dto.getMois());
        entity.setDateEmissionsDevis(dto.getDateEmissionsDevis());
        entity.setStatusProjet(dto.getStatusProjet());
        entity.setGrossisteOperateur(dto.getGrossisteOperateur());
        entity.setRevendeur(dto.getRevendeur());
        entity.setClientFinal(dto.getClientFinal());
        entity.setNomSolution(dto.getNomSolution());
        entity.setRefSolution(dto.getRefSolution());
        entity.setQuantiter(dto.getQuantiter());
        entity.setDureeAnnee(dto.getDureeAnnee());
        entity.setPaGrossiste(dto.getPaGrossiste());
        entity.setTotalCAPotentiel(dto.getTotalCAPotentiel());
        entity.setDateFinValiditeCotation(dto.getDateFinValiditeCotation());
        entity.setContactKnox(dto.getContactKnox());
        entity.setKamEndCustomer(dto.getKamEndCustomer());
        entity.setRdvClient(dto.getRdvClient());
        entity.setCommentaires(dto.getCommentaires());
    }
    
    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return null;
    }
    
    private Integer getIntegerValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }
    
    private LocalDate getDateValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }
    
    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        return null;
    }
}
