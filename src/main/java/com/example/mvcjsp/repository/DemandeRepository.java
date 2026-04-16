package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findTop10ByOrderByDateDemandeDesc();
}
