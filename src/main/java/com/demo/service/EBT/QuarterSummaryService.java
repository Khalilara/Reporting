package com.demo.service.EBT;

import com.demo.Model.EBT.QuarterSummary;
import com.demo.Repository.EBT.QuarterSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

import java.util.Optional;

@Service
public class QuarterSummaryService {

    @Autowired
    private QuarterSummaryRepository quarterSummaryRepository;

    // GET by quarter
    public QuarterSummary getByQuarter(String quarter) {
        Optional<QuarterSummary> existing = quarterSummaryRepository.findByQuarter(quarter);
        if (existing.isPresent()) {
            return existing.get();
        }
        // Si n'existe pas, créer un nouveau avec targetAmount null
        QuarterSummary newSummary = new QuarterSummary(quarter, null);
        return quarterSummaryRepository.save(newSummary);
    }

    // UPDATE target amount
    public QuarterSummary updateTargetAmount(String quarter, Double targetAmount) {
        QuarterSummary summary = getByQuarter(quarter);
        summary.setTargetAmount(targetAmount);
        return quarterSummaryRepository.save(summary);
    }

    // GET by ID
    public QuarterSummary getById(Long id) {
        return quarterSummaryRepository.findById(id).orElse(null);
    }
    public List<QuarterSummary> getAllQuarters() {
    return quarterSummaryRepository.findAll();
}
}
