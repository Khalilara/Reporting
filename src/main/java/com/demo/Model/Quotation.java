package com.demo.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quotations")
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "annee")
    private Integer annee;
    
    @Column(name = "mois")
    private String mois;
    
    @Column(name = "date_emissions_devis")
    private LocalDate dateEmissionsDevis;
    
    @Column(name = "status_projet")
    private String statusProjet;
    
    @Column(name = "grossiste_operateur")
    private String grossisteOperateur;
    
    @Column(name = "revendeur")
    private String revendeur;
    
    @Column(name = "client_final")
    private String clientFinal;
    
    @Column(name = "nom_solution")
    private String nomSolution;
    
    @Column(name = "ref_solution")
    private String refSolution;
    
    @Column(name = "quantiter")
    private Integer quantiter;
    
    @Column(name = "duree_annee")
    private Integer dureeAnnee;
    
    @Column(name = "pa_grossiste", precision = 10, scale = 2)
    private BigDecimal paGrossiste;
    
    @Column(name = "total_ca_potentiel", precision = 15, scale = 2)
    private BigDecimal totalCAPotentiel;
    
    @Column(name = "date_fin_validite_cotation")
    private LocalDate dateFinValiditeCotation;
    
    @Column(name = "contact_knox")
    private String contactKnox;
    
    @Column(name = "kam_end_customer")
    private String kamEndCustomer;
    
    @Column(name = "rdv_client")
    private LocalDate rdvClient;
    
    @Column(name = "commentaires", length = 1000)
    private String commentaires;
    
    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

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
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}
