package com.example.mvcjsp.dto;

import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.enums.DemandeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DemandeStatusResponse {
    private final Long id;
    private final DemandeStatus status;
    private final LocalDateTime date;
    private final String type;
    private final DemandeurInfo demandeur;

    public DemandeStatusResponse(Long id, DemandeStatus status, LocalDateTime date, String type, DemandeurInfo demandeur) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.type = type;
        this.demandeur = demandeur;
    }

    public static DemandeStatusResponse from(Demande demande) {
        DemandeurInfo demandeur = null;
        if (demande.getPersonne() != null) {
            String nationalite = null;
            if (demande.getPersonne().getNationalite() != null) {
                nationalite = demande.getPersonne().getNationalite().getLibelle();
            }
            demandeur = new DemandeurInfo(
                    demande.getPersonne().getNom(),
                    demande.getPersonne().getPrenom(),
                    nationalite,
                    demande.getPersonne().getDateNaissance(),
                    demande.getPersonne().getEmail(),
                    demande.getPersonne().getTelephone()
            );
        }

        String type = null;
        if (demande.getTypeDemande() != null && demande.getTypeDemande().getLibelle() != null) {
            type = demande.getTypeDemande().getLibelle().toString();
        }

        return new DemandeStatusResponse(
                demande.getId(),
                demande.getStatut(),
                demande.getDateDemande(),
                type,
                demandeur
        );
    }

    public Long getId() { return id; }
    public DemandeStatus getStatus() { return status; }
    public LocalDateTime getDate() { return date; }
    public String getType() { return type; }
    public DemandeurInfo getDemandeur() { return demandeur; }

    public static class DemandeurInfo {
        private final String nom;
        private final String prenom;
        private final String nationalite;
        private final LocalDate dateNaissance;
        private final String email;
        private final String telephone;

        public DemandeurInfo(
                String nom,
                String prenom,
                String nationalite,
                LocalDate dateNaissance,
                String email,
                String telephone
        ) {
            this.nom = nom;
            this.prenom = prenom;
            this.nationalite = nationalite;
            this.dateNaissance = dateNaissance;
            this.email = email;
            this.telephone = telephone;
        }

        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getNationalite() { return nationalite; }
        public LocalDate getDateNaissance() { return dateNaissance; }
        public String getEmail() { return email; }
        public String getTelephone() { return telephone; }
    }
}
