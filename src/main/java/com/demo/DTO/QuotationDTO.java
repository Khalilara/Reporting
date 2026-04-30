package com.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class QuotationDTO {
    
    private Long id;
    private Integer annee;
    private String mois;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateEmissionsDevis;
    
    private String statusProjet;
    private String grossisteOperateur;
    private String revendeur;
    private String clientFinal;
    private String nomSolution;
    private String refSolution;
    private Integer quantiter;
    private Integer dureeAnnee;
    private BigDecimal paGrossiste;
    private BigDecimal totalCAPotentiel;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateFinValiditeCotation;
    
    private String contactKnox;
    private String kamEndCustomer;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate rdvClient;
    
    private String commentaires;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    
    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }
    
    public LocalDate getDateEmissionsDevis() { return dateEmissionsDevis; }
    public void setDateEmissionsDevis(LocalDate dateEmissionsDevis) { this.dateEmissionsDevis = dateEmissionsDevis; }
    
    public String getStatusProjet() { return statusProjet; }
    public void setStatusProjet(String statusProjet) { this.statusProjet = statusProjet; }
    
    public String getGrossisteOperateur() { return grossisteOperateur; }
    public void setGrossisteOperateur(String grossisteOperateur) { this.grossisteOperateur = grossisteOperateur; }
    
    public String getRevendeur() { return revendeur; }
    public void setRevendeur(String revendeur) { this.revendeur = revendeur; }
    
    public String getClientFinal() { return clientFinal; }
    public void setClientFinal(String clientFinal) { this.clientFinal = clientFinal; }
    
    public String getNomSolution() { return nomSolution; }
    public void setNomSolution(String nomSolution) { this.nomSolution = nomSolution; }
    
    public String getRefSolution() { return refSolution; }
    public void setRefSolution(String refSolution) { this.refSolution = refSolution; }
    
    public Integer getQuantiter() { return quantiter; }
    public void setQuantiter(Integer quantiter) { this.quantiter = quantiter; }
    
    public Integer getDureeAnnee() { return dureeAnnee; }
    public void setDureeAnnee(Integer dureeAnnee) { this.dureeAnnee = dureeAnnee; }
    
    public BigDecimal getPaGrossiste() { return paGrossiste; }
    public void setPaGrossiste(BigDecimal paGrossiste) { this.paGrossiste = paGrossiste; }
    
    public BigDecimal getTotalCAPotentiel() { return totalCAPotentiel; }
    public void setTotalCAPotentiel(BigDecimal totalCAPotentiel) { this.totalCAPotentiel = totalCAPotentiel; }
    
    public LocalDate getDateFinValiditeCotation() { return dateFinValiditeCotation; }
    public void setDateFinValiditeCotation(LocalDate dateFinValiditeCotation) { this.dateFinValiditeCotation = dateFinValiditeCotation; }
    
    public String getContactKnox() { return contactKnox; }
    public void setContactKnox(String contactKnox) { this.contactKnox = contactKnox; }
    
    public String getKamEndCustomer() { return kamEndCustomer; }
    public void setKamEndCustomer(String kamEndCustomer) { this.kamEndCustomer = kamEndCustomer; }
    
    public LocalDate getRdvClient() { return rdvClient; }
    public void setRdvClient(LocalDate rdvClient) { this.rdvClient = rdvClient; }
    
    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }
}
