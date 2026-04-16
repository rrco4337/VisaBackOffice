package com.example.mvcjsp.model;

import com.example.mvcjsp.model.enums.ProfilTypeCode;
import jakarta.persistence.*;

@Entity
@Table(name = "type_profil")
public class TypeProfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ProfilTypeCode libelle;

    public Long getId() { return id; }
    public ProfilTypeCode getLibelle() { return libelle; }
    public void setLibelle(ProfilTypeCode libelle) { this.libelle = libelle; }
}
