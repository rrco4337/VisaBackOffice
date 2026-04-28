package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.PieceJustificative;
import com.example.mvcjsp.model.TypeDemande;
import com.example.mvcjsp.model.TypeProfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {
    List<PieceJustificative> findByTypeDemandeAndTypeProfilAndObligatoireTrueOrderByIdAsc(TypeDemande typeDemande, TypeProfil typeProfil);
    List<PieceJustificative> findByTypeDemandeAndTypeProfilOrderByIdAsc(TypeDemande typeDemande, TypeProfil typeProfil);
}
