package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.FichierDossier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FichierDossierRepository extends JpaRepository<FichierDossier, Long> {
    List<FichierDossier> findByDemandeIdOrderByDateUploadDesc(Long demandeId);
    long countByDemandeId(Long demandeId);
    void deleteByDemandeId(Long demandeId);
}
