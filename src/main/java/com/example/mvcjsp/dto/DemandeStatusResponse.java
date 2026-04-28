package com.example.mvcjsp.dto;

import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.enums.DemandeStatus;

import java.time.LocalDateTime;

public class DemandeStatusResponse {
    private final Long id;
    private final DemandeStatus status;
    private final LocalDateTime date;

    public DemandeStatusResponse(Long id, DemandeStatus status, LocalDateTime date) {
        this.id = id;
        this.status = status;
        this.date = date;
    }

    public static DemandeStatusResponse from(Demande demande) {
        return new DemandeStatusResponse(demande.getId(), demande.getStatut(), demande.getDateDemande());
    }

    public Long getId() { return id; }
    public DemandeStatus getStatus() { return status; }
    public LocalDateTime getDate() { return date; }
}
