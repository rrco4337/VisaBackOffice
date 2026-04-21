package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.TypeProfil;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeProfilRepository extends JpaRepository<TypeProfil, Long> {
    Optional<TypeProfil> findByLibelle(ProfilTypeCode libelle);
}
