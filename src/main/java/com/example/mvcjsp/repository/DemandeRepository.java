package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findTop10ByOrderByDateDemandeDesc();

    @Query("""
            select d from Demande d
            join d.visa v
            join v.passeport p
            where p.numero = :numero
            order by d.dateDemande desc
            """)
    List<Demande> findByPasseportNumeroOrderByDateDemandeDesc(@Param("numero") String numero);
}
