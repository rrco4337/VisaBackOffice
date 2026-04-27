package com.example.mvcjsp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_piece")
public class DemandePiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demande_id")
    private Demande demande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "piece_id")
    private PieceJustificative piece;

    private boolean fournie;

    @Column(name = "date_fourniture")
    private LocalDate dateFourniture;

    @Column(nullable = true)
    private boolean scanned = false;

    @Column(name = "date_scan", nullable = true)
    private LocalDateTime dateScan;

    public Long getId() { return id; }
    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }
    public PieceJustificative getPiece() { return piece; }
    public void setPiece(PieceJustificative piece) { this.piece = piece; }
    public boolean isFournie() { return fournie; }
    public void setFournie(boolean fournie) { this.fournie = fournie; }
    public LocalDate getDateFourniture() { return dateFourniture; }
    public void setDateFourniture(LocalDate dateFourniture) { this.dateFourniture = dateFourniture; }
    public boolean isScanned() { return scanned; }
    public void setScanned(boolean scanned) { this.scanned = scanned; }
    public LocalDateTime getDateScan() { return dateScan; }
    public void setDateScan(LocalDateTime dateScan) { this.dateScan = dateScan; }
}
