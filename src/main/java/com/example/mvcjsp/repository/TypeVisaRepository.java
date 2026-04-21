package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.TypeVisa;
import com.example.mvcjsp.model.enums.VisaTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeVisaRepository extends JpaRepository<TypeVisa, Long> {
    Optional<TypeVisa> findByLibelle(VisaTypeCode libelle);
}
