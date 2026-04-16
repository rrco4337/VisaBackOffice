package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Passeport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasseportRepository extends JpaRepository<Passeport, Long> {
}
