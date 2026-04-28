package com.example.mvcjsp.service;

import com.example.mvcjsp.dto.EnregistrementDemandeForm;
import com.example.mvcjsp.dto.ValidationResult;
import com.example.mvcjsp.model.*;
import com.example.mvcjsp.model.enums.DemandeStatus;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.VisaTypeCode;
import com.example.mvcjsp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnregistrementDemandeService {

    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final VisaRepository visaRepository;
    private final DemandeRepository demandeRepository;
    private final DemandePieceRepository demandePieceRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final TypeProfilRepository typeProfilRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;

    public EnregistrementDemandeService(
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            VisaRepository visaRepository,
            DemandeRepository demandeRepository,
            DemandePieceRepository demandePieceRepository,
            TypeDemandeRepository typeDemandeRepository,
            TypeProfilRepository typeProfilRepository,
            TypeVisaRepository typeVisaRepository,
            PieceJustificativeRepository pieceJustificativeRepository,
            NationaliteRepository nationaliteRepository,
            SituationFamilialeRepository situationFamilialeRepository
    ) {
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.visaRepository = visaRepository;
        this.demandeRepository = demandeRepository;
        this.demandePieceRepository = demandePieceRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.typeProfilRepository = typeProfilRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
    }

    public ValidationResult validate(EnregistrementDemandeForm form) {
        ValidationResult result = new ValidationResult();

        validateCivilState(form, result);
        validateVisa(form, result);
        validateSansDonneesRule(form, result);
        validateDemandeSource(form, result);
        validatePiecesJustificatives(form, result);

        return result;
    }

    private void validatePiecesJustificatives(EnregistrementDemandeForm form, ValidationResult result) {
        if (form.getTypeDemande() == null || form.getTypeProfil() == null) {
            return;
        }

        TypeDemande typeDemande = typeDemandeRepository.findByLibelle(form.getTypeDemande()).orElse(null);
        TypeProfil typeProfil = typeProfilRepository.findByLibelle(form.getTypeProfil()).orElse(null);

        if (typeDemande == null || typeProfil == null) {
            result.addError("Type de demande ou profil invalide.");
            return;
        }

        List<PieceJustificative> requiredPieces = pieceJustificativeRepository
                .findByTypeDemandeAndTypeProfilAndObligatoireTrueOrderByIdAsc(typeDemande, typeProfil);

        if (requiredPieces.isEmpty()) {
            return;
        }

        Set<Long> selected = new HashSet<>(form.getPieceIds());
        List<String> missingPieces = requiredPieces.stream()
                .filter(piece -> !selected.contains(piece.getId()))
                .map(PieceJustificative::getLibelle)
                .collect(Collectors.toList());

        if (!missingPieces.isEmpty()) {
            result.addError("Les pieces justificatives obligatoires suivantes doivent etre fournies : " + String.join(", ", missingPieces));
        }
    }

    @Transactional
    public Demande enregistrer(EnregistrementDemandeForm form) {
        ValidationResult validationResult = validate(form);
        if (validationResult.hasErrors()) {
            throw new BusinessException(String.join(" | ", validationResult.getErrors()));
        }

        TypeDemande typeDemande = typeDemandeRepository.findByLibelle(form.getTypeDemande())
                .orElseThrow(() -> new BusinessException("Type de demande inconnu."));
        TypeProfil typeProfil = typeProfilRepository.findByLibelle(form.getTypeProfil())
                .orElseThrow(() -> new BusinessException("Type de profil inconnu."));
        TypeVisa typeVisa = typeVisaRepository.findByLibelle(VisaTypeCode.VISA_TRANSFORMABLE)
                .orElseThrow(() -> new BusinessException("Type de visa transformable introuvable."));

        Demandeur demandeur;
        if (form.getPersonneId() != null) {
            demandeur = demandeurRepository.findById(form.getPersonneId()).orElse(new Demandeur());
        } else {
            demandeur = new Demandeur();
        }
        demandeur.setNom(form.getNom());
        demandeur.setPrenom(form.getPrenom());
        demandeur.setNomJeuneFille(form.getNomJeuneFille());
        demandeur.setNomPere(form.getNomPere());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setSituationFamiliale(form.getSituationFamiliale() != null ? situationFamilialeRepository.findById(form.getSituationFamiliale()).orElse(null) : null);
        demandeur.setNationalite(form.getNationalite() != null ? nationaliteRepository.findById(form.getNationalite()).orElse(null) : null);
        demandeur.setProfession(form.getProfession());
        demandeur.setAdresse(form.getAdresse());
        demandeur.setEmail(form.getEmail());
        demandeur.setTelephone(form.getTelephone());
        demandeur = demandeurRepository.save(demandeur);

        Passeport passeport = passeportRepository.findByNumero(form.getNumeroPasseport()).orElse(new Passeport());
        passeport.setPersonne(demandeur);
        passeport.setNumero(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrancePasseport());
        passeport.setDateExpiration(form.getDateExpirationPasseport());
        passeport.setEstActif(true);
        passeport = passeportRepository.save(passeport);

        Visa visa = null;
        if (passeport.getId() != null) {
            visa = visaRepository.findFirstByPasseportOrderByDateExpirationDesc(passeport).orElse(null);
        }
        if (visa == null) {
            visa = new Visa();
        }
        visa.setPasseport(passeport);
        visa.setTypeVisa(typeVisa);
        visa.setNumeroVisa(form.getNumeroVisa());
        visa.setDateEntree(form.getDateEntree());
        visa.setLieuEntree(form.getLieuEntree());
        visa.setDateExpiration(form.getDateExpirationVisa());
        visa.setTransformable(true);
        visa.setStatut("ACTIF");
        visa = visaRepository.save(visa);

        DemandeStatus statut = DemandeStatus.CREER;
        if (form.isSansDonnees() && 
            (form.getTypeDemande() == DemandeTypeCode.DUPLICATA || 
             form.getTypeDemande() == DemandeTypeCode.TRANSFERT_VISA ||
             form.getTypeDemande() == DemandeTypeCode.TRANSFERT_VISA_CARTE_RESIDENT)) {
            statut = DemandeStatus.APPROUVEE;
        }

        Demande demande = new Demande();
        demande.setPersonne(demandeur);
        demande.setVisa(visa);
        demande.setTypeDemande(typeDemande);
        demande.setTypeProfil(typeProfil);
        demande.setSansDonnees(form.isSansDonnees());
        demande.setStatut(statut);
        demande.setDateDemande(LocalDateTime.now());

        // Lier au dossier original si c'est un duplicata/transfert
        if (form.getDemandeOriginalId() != null) {
            Demande demandeSource = demandeRepository.findById(form.getDemandeOriginalId())
                    .orElseThrow(() -> new BusinessException("Dossier source introuvable."));
            demande.setDemandeOriginale(demandeSource);
        }

        demande = demandeRepository.save(demande);

        Set<Long> selectedPieceIds = new HashSet<>(form.getPieceIds());
        List<PieceJustificative> pieces = pieceJustificativeRepository
                .findByTypeDemandeAndTypeProfilOrderByIdAsc(typeDemande, typeProfil);

        for (PieceJustificative piece : pieces) {
            DemandePiece demandePiece = new DemandePiece();
            demandePiece.setDemande(demande);
            demandePiece.setPiece(piece);
            boolean fournie = selectedPieceIds.contains(piece.getId());
            demandePiece.setFournie(fournie);
            demandePiece.setDateFourniture(fournie ? LocalDate.now() : null);
            demandePieceRepository.save(demandePiece);
        }

        return demande;
    }

    public List<Demande> recentes() {
        return demandeRepository.findTop10ByOrderByDateDemandeDesc();
    }

    public List<PieceJustificative> piecesPour(DemandeTypeCode typeDemandeCode, com.example.mvcjsp.model.enums.ProfilTypeCode profilTypeCode) {
        if (typeDemandeCode == null || profilTypeCode == null) {
            return List.of();
        }

        TypeDemande typeDemande = typeDemandeRepository.findByLibelle(typeDemandeCode)
                .orElseThrow(() -> new BusinessException("Type de demande inconnu."));
        TypeProfil typeProfil = typeProfilRepository.findByLibelle(profilTypeCode)
                .orElseThrow(() -> new BusinessException("Type de profil inconnu."));

        return pieceJustificativeRepository.findByTypeDemandeAndTypeProfilOrderByIdAsc(typeDemande, typeProfil);
    }

    public EnregistrementDemandeForm preparerFormulaireDepuisPasseport(String numeroPasseport) {
        EnregistrementDemandeForm form = new EnregistrementDemandeForm();
        passeportRepository.findByNumero(numeroPasseport).ifPresent(passeport -> {
            Demandeur d = passeport.getPersonne();
            if (d != null) {
                form.setPersonneId(d.getId());
                form.setNom(d.getNom());
                form.setPrenom(d.getPrenom());
                form.setNomJeuneFille(d.getNomJeuneFille());
                form.setNomPere(d.getNomPere());
                form.setDateNaissance(d.getDateNaissance());
                if (d.getSituationFamiliale() != null) form.setSituationFamiliale(d.getSituationFamiliale().getId());
                if (d.getNationalite() != null) form.setNationalite(d.getNationalite().getId());
                form.setProfession(d.getProfession());
                form.setAdresse(d.getAdresse());
                form.setEmail(d.getEmail());
                form.setTelephone(d.getTelephone());
            }
            
            form.setNumeroPasseport(passeport.getNumero());
            form.setDateDelivrancePasseport(passeport.getDateDelivrance());
            form.setDateExpirationPasseport(passeport.getDateExpiration());
            
            visaRepository.findFirstByPasseportOrderByDateExpirationDesc(passeport).ifPresent(visa -> {
                form.setNumeroVisa(visa.getNumeroVisa());
                form.setDateEntree(visa.getDateEntree());
                form.setLieuEntree(visa.getLieuEntree());
                form.setDateExpirationVisa(visa.getDateExpiration());
            });
        });
        return form;
    }

    private void validateCivilState(EnregistrementDemandeForm form, ValidationResult result) {
        if (isBlank(form.getNom())) result.addError("Le nom est obligatoire.");
        if (isBlank(form.getPrenom())) result.addError("Le prenom est obligatoire.");
        if (form.getDateNaissance() == null) result.addError("La date de naissance est obligatoire.");
        if (form.getSituationFamiliale() == null) result.addError("La situation familiale est obligatoire.");
        if (form.getNationalite() == null) result.addError("La nationalite est obligatoire.");
        if (isBlank(form.getProfession())) result.addError("La profession est obligatoire.");
        if (isBlank(form.getAdresse())) result.addError("L'adresse est obligatoire.");
        if (isBlank(form.getEmail())) result.addError("L'email est obligatoire.");
        if (isBlank(form.getTelephone())) result.addError("Le telephone est obligatoire.");

        if (isBlank(form.getNumeroPasseport())) result.addError("Le numero de passeport est obligatoire.");
        if (form.getDateDelivrancePasseport() == null) result.addError("La date de delivrance du passeport est obligatoire.");
        if (form.getDateExpirationPasseport() == null) result.addError("La date d'expiration du passeport est obligatoire.");

        if (isBlank(form.getNumeroVisa())) result.addError("Le numero de visa est obligatoire.");
        if (form.getDateEntree() == null) result.addError("La date d'entree a Ivato est obligatoire.");
        if (isBlank(form.getLieuEntree())) result.addError("Le lieu d'entree est obligatoire.");
        if (form.getDateExpirationVisa() == null) result.addError("La date d'expiration du visa est obligatoire.");

        if (form.getTypeDemande() == null) result.addError("Le type de demande est obligatoire.");
        if (form.getTypeProfil() == null) result.addError("Le type de profil est obligatoire.");
    }

    private void validateVisa(EnregistrementDemandeForm form, ValidationResult result) {
        if (form.getDateEntree() == null || form.getDateExpirationVisa() == null) {
            return;
        }

        LocalDate now = LocalDate.now();

        if (form.getDateExpirationVisa().isBefore(now)) {
            result.addError("Le visa transformable est deja expire. Hors transformation classique.");
        }

        LocalDate maxExpiration = form.getDateEntree().plusMonths(3);
        if (form.getDateExpirationVisa().isAfter(maxExpiration)) {
            result.addError("Le visa transformable doit etre valide au maximum 3 mois apres la date d'entree.");
        }
    }

    private void validateDemandeSource(EnregistrementDemandeForm form, ValidationResult result) {
        if (!form.isSansDonnees()) {
            return;
        }

        Set<DemandeTypeCode> typesRequiringSource = Set.of(
                DemandeTypeCode.DUPLICATA,
                DemandeTypeCode.TRANSFERT_VISA,
                DemandeTypeCode.TRANSFERT_VISA_CARTE_RESIDENT
        );

        if (!typesRequiringSource.contains(form.getTypeDemande())) {
            return;
        }

        if (form.getDemandeOriginalId() == null) {
            result.addError("Le dossier source est obligatoire pour un duplicata ou transfert.");
            return;
        }

        Demande demandeSource = demandeRepository.findById(form.getDemandeOriginalId()).orElse(null);
        if (demandeSource == null) {
            result.addError("Le dossier source (ID: " + form.getDemandeOriginalId() + ") n'existe pas.");
        }
    }

    private void validateSansDonneesRule(EnregistrementDemandeForm form, ValidationResult result) {
        if (!form.isSansDonnees()) {
            return;
        }

        Set<DemandeTypeCode> allowed = Set.of(
                DemandeTypeCode.DUPLICATA,
                DemandeTypeCode.TRANSFERT_VISA,
                DemandeTypeCode.TRANSFERT_VISA_CARTE_RESIDENT
        );

        if (!allowed.contains(form.getTypeDemande())) {
            result.addError("L'option sans donnees est autorisee uniquement pour duplicata et transfert.");
        }

        if (form.getPieceIds() == null) {
            form.setPieceIds(List.of());
        } else {
            form.setPieceIds(form.getPieceIds().stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
