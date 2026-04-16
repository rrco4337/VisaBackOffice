package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Demandeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeurRepository extends JpaRepository<Demandeur, Long> {
}
