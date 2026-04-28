package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Visa;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mvcjsp.model.Passeport;
import java.util.Optional;

public interface VisaRepository extends JpaRepository<Visa, Long> {
    Optional<Visa> findFirstByPasseportOrderByDateExpirationDesc(Passeport passeport);
}
