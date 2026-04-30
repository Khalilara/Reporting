package com.demo.service;

import com.demo.Model.Pipe;
import com.demo.dto.PipeDTO;
import com.demo.Repository.PipeRepository;
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
public class PipeService {
    
    private static final Logger logger = Logger.getLogger(PipeService.class.getName());
    
    @Autowired
    private PipeRepository pipeRepository;
    
    public Page<Pipe> getAllPipes(Pageable pageable) {
        return pipeRepository.findAll(pageable);
    }
    
    public Pipe getPipeById(Long id) {
        return pipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pipe not found: " + id));
    }
    
    public Pipe createPipe(PipeDTO dto) {
        Pipe pipe = new Pipe();
        mapDtoToEntity(dto, pipe);
        return pipeRepository.save(pipe);
    }
    
    public Pipe updatePipe(Long id, PipeDTO dto) {
        Pipe pipe = getPipeById(id);
        mapDtoToEntity(dto, pipe);
        return pipeRepository.save(pipe);
    }
    
    public void deletePipe(Long id) {
        pipeRepository.deleteById(id);
    }
    
    public List<Pipe> uploadPipesFromExcel(MultipartFile file) throws IOException {
        List<Pipe> pipes = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    Pipe pipe = new Pipe();
                    
                    pipe.setAnnee(getIntegerValue(row.getCell(0)));
                    pipe.setMois(getStringValue(row.getCell(1)));
                    pipe.setDateEmissionDevis(getDateValue(row.getCell(2)));
                    pipe.setStatutProjet(getStringValue(row.getCell(3)));
                    pipe.setGrossisteOperateur(getStringValue(row.getCell(4)));
                    pipe.setRevendeur(getStringValue(row.getCell(5)));
                    pipe.setClientFinal(getStringValue(row.getCell(6)));
                    pipe.setNomSolution(getStringValue(row.getCell(7)));
                    pipe.setRefSolution(getStringValue(row.getCell(8)));
                    pipe.setQte(getIntegerValue(row.getCell(9)));
                    pipe.setDureeAnnee(getIntegerValue(row.getCell(10)));
                    pipe.setPaGrossiste(getBigDecimalValue(row.getCell(11)));
                    pipe.setTotalCAHTpotentiel(getBigDecimalValue(row.getCell(12)));
                    pipe.setDateFinValiditeCotation(getDateValue(row.getCell(13)));
                    pipe.setContactKnox(getStringValue(row.getCell(14)));
                    pipe.setKamEndCustomer(getStringValue(row.getCell(15)));
                    pipe.setRdvClient(getDateValue(row.getCell(16)));
                    pipe.setCommentaires(getStringValue(row.getCell(17)));
                    
                    pipes.add(pipe);
                } catch (Exception e) {
                    logger.warning("Erreur lors du traitement de la ligne " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        
        return pipeRepository.saveAll(pipes);
    }
    
    private void mapDtoToEntity(PipeDTO dto, Pipe entity) {
        entity.setAnnee(dto.getAnnee());
        entity.setMois(dto.getMois());
        entity.setDateEmissionDevis(dto.getDateEmissionDevis());
        entity.setStatutProjet(dto.getStatutProjet());
        entity.setGrossisteOperateur(dto.getGrossisteOperateur());
        entity.setRevendeur(dto.getRevendeur());
        entity.setClientFinal(dto.getClientFinal());
        entity.setNomSolution(dto.getNomSolution());
        entity.setRefSolution(dto.getRefSolution());
        entity.setQte(dto.getQte());
        entity.setDureeAnnee(dto.getDureeAnnee());
        entity.setPaGrossiste(dto.getPaGrossiste());
        entity.setTotalCAHTpotentiel(dto.getTotalCAHTpotentiel());
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
