package com.example.mvcjsp.dto;

import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnregistrementDemandeForm {
    private String nom;
    private String prenom;
    private String nomJeuneFille;
    private String nomPere;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private Long situationFamiliale;
    private Long nationalite;
    private String profession;
    private String adresse;
    private String email;
    private String telephone;

    private String numeroPasseport;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDelivrancePasseport;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpirationPasseport;

    private String numeroVisa;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEntree;

    private String lieuEntree;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpirationVisa;

    private DemandeTypeCode typeDemande;
    private ProfilTypeCode typeProfil;
    private boolean sansDonnees;
    private List<Long> pieceIds = new ArrayList<>();

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = nomJeuneFille; }
    public String getNomPere() { return nomPere; }
    public void setNomPere(String nomPere) { this.nomPere = nomPere; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public Long getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(Long situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public Long getNationalite() { return nationalite; }
    public void setNationalite(Long nationalite) { this.nationalite = nationalite; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getNumeroPasseport() { return numeroPasseport; }
    public void setNumeroPasseport(String numeroPasseport) { this.numeroPasseport = numeroPasseport; }
    public LocalDate getDateDelivrancePasseport() { return dateDelivrancePasseport; }
    public void setDateDelivrancePasseport(LocalDate dateDelivrancePasseport) { this.dateDelivrancePasseport = dateDelivrancePasseport; }
    public LocalDate getDateExpirationPasseport() { return dateExpirationPasseport; }
    public void setDateExpirationPasseport(LocalDate dateExpirationPasseport) { this.dateExpirationPasseport = dateExpirationPasseport; }
    public String getNumeroVisa() { return numeroVisa; }
    public void setNumeroVisa(String numeroVisa) { this.numeroVisa = numeroVisa; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }
    public String getLieuEntree() { return lieuEntree; }
    public void setLieuEntree(String lieuEntree) { this.lieuEntree = lieuEntree; }
    public LocalDate getDateExpirationVisa() { return dateExpirationVisa; }
    public void setDateExpirationVisa(LocalDate dateExpirationVisa) { this.dateExpirationVisa = dateExpirationVisa; }
    public DemandeTypeCode getTypeDemande() { return typeDemande; }
    public void setTypeDemande(DemandeTypeCode typeDemande) { this.typeDemande = typeDemande; }
    public ProfilTypeCode getTypeProfil() { return typeProfil; }
    public void setTypeProfil(ProfilTypeCode typeProfil) { this.typeProfil = typeProfil; }
    public boolean isSansDonnees() { return sansDonnees; }
    public void setSansDonnees(boolean sansDonnees) { this.sansDonnees = sansDonnees; }
    public List<Long> getPieceIds() { return pieceIds; }
    public void setPieceIds(List<Long> pieceIds) { this.pieceIds = pieceIds == null ? new ArrayList<>() : pieceIds; }
}
