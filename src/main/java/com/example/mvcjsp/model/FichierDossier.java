package com.example.mvcjsp.model;

import com.example.mvcjsp.model.enums.FichierCategorie;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichier_dossier")
public class FichierDossier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demande_id")
    private Demande demande;

    @ManyToOne(optional = true)
    @JoinColumn(name = "piece_id")
    private PieceJustificative piece;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    @Column(name = "type_contenu")
    private String typeContenu;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie")
    private FichierCategorie categorie;

    @Column(name = "date_upload", nullable = false)
    private LocalDateTime dateUpload;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "utilisateur_upload")
    private String utilisateurUpload;

    public Long getId() { return id; }
    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }
    public PieceJustificative getPiece() { return piece; }
    public void setPiece(PieceJustificative piece) { this.piece = piece; }
    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }
    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }
    public Long getTailleFichier() { return tailleFichier; }
    public void setTailleFichier(Long tailleFichier) { this.tailleFichier = tailleFichier; }
    public String getTypeContenu() { return typeContenu; }
    public void setTypeContenu(String typeContenu) { this.typeContenu = typeContenu; }
    public FichierCategorie getCategorie() { return categorie; }
    public void setCategorie(FichierCategorie categorie) { this.categorie = categorie; }
    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }
    public String getUtilisateurUpload() { return utilisateurUpload; }
    public void setUtilisateurUpload(String utilisateurUpload) { this.utilisateurUpload = utilisateurUpload; }
}
