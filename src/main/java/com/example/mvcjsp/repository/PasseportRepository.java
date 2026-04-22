package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasseportRepository extends JpaRepository<Passeport, Long> {
    Optional<Passeport> findByNumero(String numero);
}
