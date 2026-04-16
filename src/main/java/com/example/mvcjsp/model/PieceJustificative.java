package com.example.mvcjsp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "piece_justificative")
public class PieceJustificative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_demande_id")
    private TypeDemande typeDemande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_profil_id")
    private TypeProfil typeProfil;

    private String libelle;
    private boolean obligatoire;

    public Long getId() { return id; }
    public TypeDemande getTypeDemande() { return typeDemande; }
    public void setTypeDemande(TypeDemande typeDemande) { this.typeDemande = typeDemande; }
    public TypeProfil getTypeProfil() { return typeProfil; }
    public void setTypeProfil(TypeProfil typeProfil) { this.typeProfil = typeProfil; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
}
