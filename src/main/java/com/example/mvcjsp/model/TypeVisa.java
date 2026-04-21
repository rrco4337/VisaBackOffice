package com.example.mvcjsp.model;

import com.example.mvcjsp.model.enums.VisaTypeCode;
import jakarta.persistence.*;

@Entity
@Table(name = "type_visa")
public class TypeVisa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private VisaTypeCode libelle;

    @Column(name = "duree_validite_jours")
    private Integer dureeValiditeJours;

    public Long getId() { return id; }
    public VisaTypeCode getLibelle() { return libelle; }
    public void setLibelle(VisaTypeCode libelle) { this.libelle = libelle; }
    public Integer getDureeValiditeJours() { return dureeValiditeJours; }
    public void setDureeValiditeJours(Integer dureeValiditeJours) { this.dureeValiditeJours = dureeValiditeJours; }
}
