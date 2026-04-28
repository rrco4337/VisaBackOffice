package com.example.mvcjsp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nationalite")
public class Nationalite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    public Long getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
