package com.example.mvcjsp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visa")
public class Visa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passeport_id")
    private Passeport passeport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_visa_id")
    private TypeVisa typeVisa;

    @Column(name = "numero_visa")
    private String numeroVisa;

    @Column(name = "date_entree")
    private LocalDate dateEntree;

    @Column(name = "lieu_entree")
    private String lieuEntree;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    private String statut;
    private boolean transformable;

    public Long getId() { return id; }
    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) { this.passeport = passeport; }
    public TypeVisa getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeVisa typeVisa) { this.typeVisa = typeVisa; }
    public String getNumeroVisa() { return numeroVisa; }
    public void setNumeroVisa(String numeroVisa) { this.numeroVisa = numeroVisa; }
    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }
    public String getLieuEntree() { return lieuEntree; }
    public void setLieuEntree(String lieuEntree) { this.lieuEntree = lieuEntree; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public boolean isTransformable() { return transformable; }
    public void setTransformable(boolean transformable) { this.transformable = transformable; }
}
