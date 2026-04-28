package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Nationalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationaliteRepository extends JpaRepository<Nationalite, Long> {
}
