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
import com.example.mvcjsp.repository.NationaliteRepository;
import com.example.mvcjsp.repository.SituationFamilialeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

// @Component - DÉSACTIVÉ: Les données s'initialisent via les scripts SQL (bd/*.sql)
// Pour initialiser la base: exécutez bd/reset.sql ou bd/data.sql
public class DataInitializer implements CommandLineRunner {

    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final TypeProfilRepository typeProfilRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;

    public DataInitializer(
            TypeVisaRepository typeVisaRepository,
            TypeDemandeRepository typeDemandeRepository,
            TypeProfilRepository typeProfilRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            NationaliteRepository nationaliteRepository,
            SituationFamilialeRepository situationFamilialeRepository
    ) {
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.typeProfilRepository = typeProfilRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
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

        if (nationaliteRepository.count() == 0) {
            Arrays.asList("Malagasy", "Française", "Américaine", "Canadienne", "Italienne").forEach(n -> {
                com.example.mvcjsp.model.Nationalite nat = new com.example.mvcjsp.model.Nationalite();
                nat.setLibelle(n);
                nationaliteRepository.save(nat);
            });
        }

        if (situationFamilialeRepository.count() == 0) {
            Arrays.asList("Célibataire", "Marié(e)", "Divorcé(e)", "Veuf/Veuve").forEach(s -> {
                com.example.mvcjsp.model.SituationFamiliale sf = new com.example.mvcjsp.model.SituationFamiliale();
                sf.setLibelle(s);
                situationFamilialeRepository.save(sf);
            });
        }
    }

    private void seedPieces() {
        for (DemandeTypeCode demandeType : DemandeTypeCode.values()) {
            TypeDemande td = typeDemandeRepository.findByLibelle(demandeType).orElseThrow();
            for (ProfilTypeCode profilType : ProfilTypeCode.values()) {
                TypeProfil tp = typeProfilRepository.findByLibelle(profilType).orElseThrow();

                // Pièces obligatoires de base
                List<String> basePieces = Arrays.asList(
                        "Copie passeport",
                        "Copie visa transformable",
                        "Acte de naissance"
                );

                List<String> profilPieces = profilType == ProfilTypeCode.ETUDIANT
                        ? Arrays.asList("Certificat de scolarite", "Attestation de prise en charge")
                        : Arrays.asList("Contrat de travail", "Attestation employeur");

                // Pièces obligatoires spécifiques au type de demande
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

                // Pièces justificatives optionnelles
                List<String> optionalBasePieces = Arrays.asList(
                        "Lettre de motivation",
                        "Copie diplomes",
                        "Certificat de bonne conduite",
                        "Assurance voyage"
                );

                for (String libelle : optionalBasePieces) {
                    savePiece(td, tp, libelle, false);
                }

                // Pièces optionnelles spécifiques au profil
                if (profilType == ProfilTypeCode.ETUDIANT) {
                    savePiece(td, tp, "Relevé de notes", false);
                    savePiece(td, tp, "Lettre de recommandation", false);
                    savePiece(td, tp, "Plan d'etudes", false);
                } else {
                    savePiece(td, tp, "Bulletin de salaire recent", false);
                    savePiece(td, tp, "Bilan comptable entreprise", false);
                }

                // Pièces optionnelles spécifiques au type de demande
                if (demandeType == DemandeTypeCode.NOUVEAU_TITRE) {
                    savePiece(td, tp, "Justificatif de domicile", false);
                    savePiece(td, tp, "Photo d'identite couleur", false);
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
