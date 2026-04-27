package com.example.mvcjsp.repository;

import com.example.mvcjsp.model.DemandePiece;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandePieceRepository extends JpaRepository<DemandePiece, Long> {
    List<DemandePiece> findByDemandeIdAndScannedFalse(Long demandeId);
    List<DemandePiece> findByDemandeId(Long demandeId);
    long countByDemandeIdAndScannedFalse(Long demandeId);
}

