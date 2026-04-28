package com.example.mvcjsp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "passeport")
public class Passeport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personne_id")
    private Demandeur personne;

    @ManyToOne
    @JoinColumn(name = "passeport_precedent_id")
    private Passeport passeportPrecedent;

    private String numero;

    @Column(name = "date_delivrance")
    private LocalDate dateDelivrance;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Column(name = "est_actif")
    private boolean estActif;

    public Long getId() { return id; }
    public Demandeur getPersonne() { return personne; }
    public void setPersonne(Demandeur personne) { this.personne = personne; }
    public Passeport getPasseportPrecedent() { return passeportPrecedent; }
    public void setPasseportPrecedent(Passeport passeportPrecedent) { this.passeportPrecedent = passeportPrecedent; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public boolean isEstActif() { return estActif; }
    public void setEstActif(boolean estActif) { this.estActif = estActif; }
}
