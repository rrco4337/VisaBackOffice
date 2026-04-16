package com.example.mvcjsp;

import com.example.mvcjsp.model.PieceJustificative;
import com.example.mvcjsp.model.TypeDemande;
import com.example.mvcjsp.model.TypeProfil;
import com.example.mvcjsp.model.TypeVisa;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import com.example.mvcjsp.model.enums.VisaTypeCode;
import com.example.mvcjsp.repository.PieceJustificativeRepository;
import com.example.mvcjsp.repository.TypeDemandeRepository;
import com.example.mvcjsp.repository.TypeProfilRepository;
import com.example.mvcjsp.repository.TypeVisaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final TypeProfilRepository typeProfilRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;

    public DataInitializer(
            TypeVisaRepository typeVisaRepository,
            TypeDemandeRepository typeDemandeRepository,
            TypeProfilRepository typeProfilRepository,
            PieceJustificativeRepository pieceJustificativeRepository
    ) {
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.typeProfilRepository = typeProfilRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        TypeVisa typeVisa = typeVisaRepository.findByLibelle(VisaTypeCode.VISA_TRANSFORMABLE)
                .orElseGet(() -> {
                    TypeVisa tv = new TypeVisa();
                    tv.setLibelle(VisaTypeCode.VISA_TRANSFORMABLE);
                    tv.setDureeValiditeJours(90);
                    return typeVisaRepository.save(tv);
                });

        if (typeVisa == null) {
            return;
        }

        for (DemandeTypeCode code : DemandeTypeCode.values()) {
            typeDemandeRepository.findByLibelle(code).orElseGet(() -> {
                TypeDemande td = new TypeDemande();
                td.setLibelle(code);
                td.setNecessiteSansDonnees(
                        code == DemandeTypeCode.DUPLICATA
                                || code == DemandeTypeCode.TRANSFERT_VISA
                                || code == DemandeTypeCode.TRANSFERT_VISA_CARTE_RESIDENT
                );
                return typeDemandeRepository.save(td);
            });
        }

        for (ProfilTypeCode code : ProfilTypeCode.values()) {
            typeProfilRepository.findByLibelle(code).orElseGet(() -> {
                TypeProfil tp = new TypeProfil();
                tp.setLibelle(code);
                return typeProfilRepository.save(tp);
            });
        }

        if (pieceJustificativeRepository.count() == 0) {
            seedPieces();
        }
    }

    private void seedPieces() {
        for (DemandeTypeCode demandeType : DemandeTypeCode.values()) {
            TypeDemande td = typeDemandeRepository.findByLibelle(demandeType).orElseThrow();
            for (ProfilTypeCode profilType : ProfilTypeCode.values()) {
                TypeProfil tp = typeProfilRepository.findByLibelle(profilType).orElseThrow();

                List<String> basePieces = Arrays.asList(
                        "Copie passeport",
                        "Copie visa transformable",
                        "Acte de naissance"
                );

                List<String> profilPieces = profilType == ProfilTypeCode.ETUDIANT
                        ? Arrays.asList("Certificat de scolarite", "Attestation de prise en charge")
                        : Arrays.asList("Contrat de travail", "Attestation employeur");

                for (String libelle : basePieces) {
                    savePiece(td, tp, libelle, true);
                }
                for (String libelle : profilPieces) {
                    savePiece(td, tp, libelle, true);
                }

                if (demandeType == DemandeTypeCode.DUPLICATA) {
                    savePiece(td, tp, "Declaration de perte", true);
                }
                if (demandeType == DemandeTypeCode.TRANSFERT_VISA || demandeType == DemandeTypeCode.TRANSFERT_VISA_CARTE_RESIDENT) {
                    savePiece(td, tp, "Justificatif de transfert", true);
                }
            }
        }
    }

    private void savePiece(TypeDemande td, TypeProfil tp, String libelle, boolean obligatoire) {
        PieceJustificative p = new PieceJustificative();
        p.setTypeDemande(td);
        p.setTypeProfil(tp);
        p.setLibelle(libelle);
        p.setObligatoire(obligatoire);
        pieceJustificativeRepository.save(p);
    }
}
