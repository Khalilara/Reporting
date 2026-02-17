package com.demo.service.EBT;

import com.demo.Model.EBT.EbtWeeklyTableau;
import com.demo.Repository.EBT.EbtWeeklyTableauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EbtWeeklyTableauService {

    @Autowired
    private EbtWeeklyTableauRepository ebtWeeklyTableauRepository;

    // GET all by quarter
    public List<EbtWeeklyTableau> getAllByQuarter(String quarter) {
        return ebtWeeklyTableauRepository.findByQuarter(quarter);
    }

    // GET all
    public List<EbtWeeklyTableau> getAll() {
        return ebtWeeklyTableauRepository.findAll();
    }

    // CREATE
    public EbtWeeklyTableau create(EbtWeeklyTableau ebtWeeklyTableau) {
        return ebtWeeklyTableauRepository.save(ebtWeeklyTableau);
    }

    // UPDATE
    public EbtWeeklyTableau update(Long id, EbtWeeklyTableau ebtWeeklyTableau) {
        Optional<EbtWeeklyTableau> existing = ebtWeeklyTableauRepository.findById(id);
        if (existing.isPresent()) {
            EbtWeeklyTableau entity = existing.get();
            entity.setWeekNumber(ebtWeeklyTableau.getWeekNumber());
            entity.setCaWeekly(ebtWeeklyTableau.getCaWeekly());
            entity.setComment(ebtWeeklyTableau.getComment());
            entity.setQuarter(ebtWeeklyTableau.getQuarter());
            return ebtWeeklyTableauRepository.save(entity);
        }
        return null;
    }

    // PATCH UPDATE (partial)
    public EbtWeeklyTableau patchUpdate(Long id, EbtWeeklyTableau ebtWeeklyTableau) {
        Optional<EbtWeeklyTableau> existing = ebtWeeklyTableauRepository.findById(id);
        if (existing.isPresent()) {
            EbtWeeklyTableau entity = existing.get();
            if (ebtWeeklyTableau.getWeekNumber() != null) {
                entity.setWeekNumber(ebtWeeklyTableau.getWeekNumber());
            }
            if (ebtWeeklyTableau.getCaWeekly() != null) {
                entity.setCaWeekly(ebtWeeklyTableau.getCaWeekly());
            }
            if (ebtWeeklyTableau.getComment() != null) {
                entity.setComment(ebtWeeklyTableau.getComment());
            }
            if (ebtWeeklyTableau.getQuarter() != null) {
                entity.setQuarter(ebtWeeklyTableau.getQuarter());
            }
            return ebtWeeklyTableauRepository.save(entity);
        }
        return null;
    }

    // DELETE
    public void delete(Long id) {
        ebtWeeklyTableauRepository.deleteById(id);
    }
}
