package com.example.mvcjsp.model;

import com.example.mvcjsp.model.enums.DemandeTypeCode;
import jakarta.persistence.*;

@Entity
@Table(name = "type_demande")
public class TypeDemande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private DemandeTypeCode libelle;

    @Column(name = "necessite_sans_donnees")
    private boolean necessiteSansDonnees;

    public Long getId() { return id; }
    public DemandeTypeCode getLibelle() { return libelle; }
    public void setLibelle(DemandeTypeCode libelle) { this.libelle = libelle; }
    public boolean isNecessiteSansDonnees() { return necessiteSansDonnees; }
    public void setNecessiteSansDonnees(boolean necessiteSansDonnees) { this.necessiteSansDonnees = necessiteSansDonnees; }
}
