package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.FichierDossier;
import com.example.mvcjsp.model.enums.FichierCategorie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FichierDossierRepository extends JpaRepository<FichierDossier, Long> {
    List<FichierDossier> findByDemandeIdOrderByDateUploadDesc(Long demandeId);
    List<FichierDossier> findByDemandeIdAndPieceIdOrderByDateUploadDesc(Long demandeId, Long pieceId);
    long countByDemandeId(Long demandeId);
    long countByDemandeIdAndPieceId(Long demandeId, Long pieceId);
    boolean existsByDemandeIdAndPieceId(Long demandeId, Long pieceId);
    Optional<FichierDossier> findByDemandeIdAndCategorie(Long demandeId, FichierCategorie categorie);
    boolean existsByDemandeIdAndCategorie(Long demandeId, FichierCategorie categorie);
    void deleteByDemandeIdAndCategorie(Long demandeId, FichierCategorie categorie);
    void deleteByDemandeId(Long demandeId);
}
