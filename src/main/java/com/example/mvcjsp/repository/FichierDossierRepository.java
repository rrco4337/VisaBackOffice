package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.FichierDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FichierDossierRepository extends JpaRepository<FichierDossier, Long> {
    List<FichierDossier> findByDemandeIdOrderByDateUploadDesc(Long demandeId);
    List<FichierDossier> findByDemandeIdAndPieceIdOrderByDateUploadDesc(Long demandeId, Long pieceId);
    long countByDemandeId(Long demandeId);
    long countByDemandeIdAndPieceId(Long demandeId, Long pieceId);
    boolean existsByDemandeIdAndPieceId(Long demandeId, Long pieceId);
    void deleteByDemandeId(Long demandeId);
}
