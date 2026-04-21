package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.TypeDemande;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeDemandeRepository extends JpaRepository<TypeDemande, Long> {
    Optional<TypeDemande> findByLibelle(DemandeTypeCode libelle);
}
