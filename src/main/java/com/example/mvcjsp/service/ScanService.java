package com.example.mvcjsp.service;

import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.DemandePiece;
import com.example.mvcjsp.model.enums.DemandeStatus;
import com.example.mvcjsp.model.enums.FichierCategorie;
import com.example.mvcjsp.repository.DemandeRepository;
import com.example.mvcjsp.repository.DemandePieceRepository;
import com.example.mvcjsp.repository.FichierDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ScanService {
    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandePieceRepository demandePieceRepository;

    @Autowired
    private FichierDossierRepository fichierDossierRepository;

    /**
     * Mark a piece as scanned for a given demande
     * @param demandeId The ID of the demande
     * @param pieceId The ID of the piece to mark as scanned
     * @return The updated DemandePiece
     * @throws IllegalArgumentException if demande or piece not found
     * @throws IllegalStateException if demande is already scanned
     */
    public DemandePiece markPieceAsScanned(Long demandeId, Long pieceId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable: " + demandeId));

        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouillé. Aucune modification n'est possible.");
        }

        List<DemandePiece> pieces = demandePieceRepository.findByDemandeId(demandeId);
        DemandePiece targetPiece = pieces.stream()
                .filter(p -> p.getPiece().getId().equals(pieceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pièce non associée à ce dossier: " + pieceId));

        targetPiece.setScanned(true);
        targetPiece.setDateScan(LocalDateTime.now());
        return demandePieceRepository.save(targetPiece);
    }

    /**
     * Check if all pieces for a demande have been scanned
     * @param demandeId The ID of the demande
     * @return true if all pieces are scanned, false otherwise
     */
    public boolean areAllPiecesScanned(Long demandeId) {
        long unscannedCount = demandePieceRepository.countByDemandeIdAndScannedFalse(demandeId);
        return unscannedCount == 0;
    }

    /**
     * Get the count of scanned pieces for a demande
     * @param demandeId The ID of the demande
     * @return Count of scanned pieces
     */
    public long getScannedPieceCount(Long demandeId) {
        List<DemandePiece> allPieces = demandePieceRepository.findByDemandeId(demandeId);
        return allPieces.stream()
                .filter(DemandePiece::isScanned)
                .count();
    }

    /**
     * Get the total count of pieces for a demande
     * @param demandeId The ID of the demande
     * @return Total count of pieces
     */
    public long getTotalPieceCount(Long demandeId) {
        return demandePieceRepository.findByDemandeId(demandeId).size();
    }

    /**
     * Finalize scanning - mark demande as SCANNE (locked)
     * Only possible if all pieces are scanned
     * @param demandeId The ID of the demande
     * @return The updated Demande
     * @throws IllegalArgumentException if demande not found
     * @throws IllegalStateException if not all pieces are scanned
     */
    public Demande finalizeScanning(Long demandeId) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable: " + demandeId));

        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est déjà verrouillé.");
        }

        if (!areAllPiecesScanned(demandeId)) {
            long scannedCount = getScannedPieceCount(demandeId);
            long totalCount = getTotalPieceCount(demandeId);
            throw new IllegalStateException(
                    String.format("Impossible de finaliser le scan. %d/%d pièces scannées.", scannedCount, totalCount)
            );
        }

        boolean photoOk = demande.isPhotoScanned()
            || fichierDossierRepository.existsByDemandeIdAndCategorie(demandeId, FichierCategorie.PHOTO);
        boolean signatureOk = demande.isSignatureScanned()
            || fichierDossierRepository.existsByDemandeIdAndCategorie(demandeId, FichierCategorie.SIGNATURE);

        if (!photoOk || !signatureOk) {
            throw new IllegalStateException("Impossible de finaliser le scan. Photo et signature requises.");
        }

        demande.setStatut(DemandeStatus.SCANNE);
        return demandeRepository.save(demande);
    }

    /**
     * Check if a demande is locked (scanned)
     * @param demande The demande to check
     * @return true if demande status is SCANNE
     */
    public boolean isDemandeScanned(Demande demande) {
        return demande != null && demande.getStatut() == DemandeStatus.SCANNE;
    }

    /**
     * Get all unscanned pieces for a demande
     * @param demandeId The ID of the demande
     * @return List of unscanned DemandePiece entries
     */
    public List<DemandePiece> getUnscannedPieces(Long demandeId) {
        return demandePieceRepository.findByDemandeIdAndScannedFalse(demandeId);
    }
}
