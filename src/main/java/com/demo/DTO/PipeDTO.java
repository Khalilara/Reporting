package com.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class PipeDTO {
    
    private Long id;
    private Integer annee;
    private String mois;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateEmissionDevis;
    
    private String statutProjet;
    private String grossisteOperateur;
    private String revendeur;
    private String clientFinal;
    private String nomSolution;
    private String refSolution;
    private Integer qte;
    private Integer dureeAnnee;
    private BigDecimal paGrossiste;
    private BigDecimal totalCAHTpotentiel;
    
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
    
    public LocalDate getDateEmissionDevis() { return dateEmissionDevis; }
    public void setDateEmissionDevis(LocalDate dateEmissionDevis) { this.dateEmissionDevis = dateEmissionDevis; }
    
    public String getStatutProjet() { return statutProjet; }
    public void setStatutProjet(String statutProjet) { this.statutProjet = statutProjet; }
    
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
    
    public Integer getQte() { return qte; }
    public void setQte(Integer qte) { this.qte = qte; }
    
    public Integer getDureeAnnee() { return dureeAnnee; }
    public void setDureeAnnee(Integer dureeAnnee) { this.dureeAnnee = dureeAnnee; }
    
    public BigDecimal getPaGrossiste() { return paGrossiste; }
    public void setPaGrossiste(BigDecimal paGrossiste) { this.paGrossiste = paGrossiste; }
    
    public BigDecimal getTotalCAHTpotentiel() { return totalCAHTpotentiel; }
    public void setTotalCAHTpotentiel(BigDecimal totalCAHTpotentiel) { this.totalCAHTpotentiel = totalCAHTpotentiel; }
    
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
