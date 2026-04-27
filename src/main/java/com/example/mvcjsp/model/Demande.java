package com.example.mvcjsp.model;

import com.example.mvcjsp.model.enums.DemandeStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande")
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "personne_id")
    private Demandeur personne;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_demande_id")
    private TypeDemande typeDemande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_profil_id")
    private TypeProfil typeProfil;

    @ManyToOne(optional = false)
    @JoinColumn(name = "visa_id")
    private Visa visa;

    @ManyToOne(optional = true)
    @JoinColumn(name = "demande_originale_id")
    private Demande demandeOriginale;

    @Enumerated(EnumType.STRING)
    private DemandeStatus statut;

    @Column(name = "sans_donnees")
    private boolean sansDonnees;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    public Long getId() { return id; }
    public Demandeur getPersonne() { return personne; }
    public void setPersonne(Demandeur personne) { this.personne = personne; }
    public TypeDemande getTypeDemande() { return typeDemande; }
    public void setTypeDemande(TypeDemande typeDemande) { this.typeDemande = typeDemande; }
    public TypeProfil getTypeProfil() { return typeProfil; }
    public void setTypeProfil(TypeProfil typeProfil) { this.typeProfil = typeProfil; }
    public Visa getVisa() { return visa; }
    public void setVisa(Visa visa) { this.visa = visa; }
    public Demande getDemandeOriginale() { return demandeOriginale; }
    public void setDemandeOriginale(Demande demandeOriginale) { this.demandeOriginale = demandeOriginale; }
    public DemandeStatus getStatut() { return statut; }
    public void setStatut(DemandeStatus statut) { this.statut = statut; }
    public boolean isSansDonnees() { return sansDonnees; }
    public void setSansDonnees(boolean sansDonnees) { this.sansDonnees = sansDonnees; }
    public LocalDateTime getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDateTime dateDemande) { this.dateDemande = dateDemande; }
}
