package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.SituationFamiliale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SituationFamilialeRepository extends JpaRepository<SituationFamiliale, Long> {
}
