package com.example.mvcjsp.controller;

import com.example.mvcjsp.dto.EnregistrementDemandeForm;
import com.example.mvcjsp.dto.ValidationResult;
import com.example.mvcjsp.model.enums.DemandeTypeCode;
import com.example.mvcjsp.model.enums.ProfilTypeCode;
import com.example.mvcjsp.repository.NationaliteRepository;
import com.example.mvcjsp.repository.SituationFamilialeRepository;
import com.example.mvcjsp.service.EnregistrementDemandeService;
import com.example.mvcjsp.service.ScanService;
import com.example.mvcjsp.service.FileUploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.FichierDossier;
import com.example.mvcjsp.repository.DemandeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final EnregistrementDemandeService enregistrementDemandeService;
    private final ScanService scanService;
    private final FileUploadService fileUploadService;
    private final DemandeRepository demandeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;

    public UserController(
            EnregistrementDemandeService enregistrementDemandeService,
            ScanService scanService,
            FileUploadService fileUploadService,
            DemandeRepository demandeRepository,
            NationaliteRepository nationaliteRepository,
            SituationFamilialeRepository situationFamilialeRepository
    ){
        this.enregistrementDemandeService = enregistrementDemandeService;
        this.scanService = scanService;
        this.fileUploadService = fileUploadService;
        this.demandeRepository = demandeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("demandesRecentes", enregistrementDemandeService.recentes());
        return "index";
    }

    @GetMapping("/demande")
    public String preparationDemande(@RequestParam(required = false) String typeDemande,
                                     @RequestParam(required = false) String typeProfil,
                                     @RequestParam(required = false) Long personneId,
                                     Model model) {
        if (!model.containsAttribute("form")) {
            EnregistrementDemandeForm form = new EnregistrementDemandeForm();
            form.setTypeDemande(typeDemande != null ? DemandeTypeCode.valueOf(typeDemande) : DemandeTypeCode.NOUVEAU_TITRE);
            form.setTypeProfil(typeProfil != null ? ProfilTypeCode.valueOf(typeProfil) : ProfilTypeCode.ETUDIANT);
            form.setPersonneId(personneId);
            model.addAttribute("form", form);
        }

        EnregistrementDemandeForm currentForm = (EnregistrementDemandeForm) model.getAttribute("form");
        if (currentForm == null) {
            currentForm = new EnregistrementDemandeForm();
            currentForm.setTypeDemande(typeDemande != null ? DemandeTypeCode.valueOf(typeDemande) : DemandeTypeCode.NOUVEAU_TITRE);
            currentForm.setTypeProfil(typeProfil != null ? ProfilTypeCode.valueOf(typeProfil) : ProfilTypeCode.ETUDIANT);
            currentForm.setPersonneId(personneId);
            model.addAttribute("form", currentForm);
        }
        model.addAttribute("typesDemande", DemandeTypeCode.values());
        model.addAttribute("typesProfil", ProfilTypeCode.values());
        model.addAttribute("pieces", enregistrementDemandeService.piecesPour(currentForm.getTypeDemande(), currentForm.getTypeProfil()));
        model.addAttribute("nationalites", nationaliteRepository.findAll());
        model.addAttribute("situationsFamiliales", situationFamilialeRepository.findAll());

        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", List.of());
        }

        return "demande";
    }

    @GetMapping("/demande/search")
    public String searchDemande(@RequestParam("numeroPasseport") String numeroPasseport, RedirectAttributes redirectAttributes) {
        EnregistrementDemandeForm form = enregistrementDemandeService.preparerFormulaireDepuisPasseport(numeroPasseport);
        if (form.getNom() == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Aucun dossier trouvé pour le passeport : " + numeroPasseport));
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Données pré-remplies pour le passeport : " + numeroPasseport);
            redirectAttributes.addFlashAttribute("form", form);
        }
        return "redirect:/demande";
    }

    @PostMapping("/demandes/enregistrer")
    public String enregistrer(@ModelAttribute("form") EnregistrementDemandeForm form, RedirectAttributes redirectAttributes){
        ValidationResult result = enregistrementDemandeService.validate(form);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result.getErrors());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/demande";
        }

        Demande demande = enregistrementDemandeService.enregistrer(form);
        redirectAttributes.addFlashAttribute("successMessage", "Demande enregistree avec statut " + demande.getStatut() + ".");
        return "redirect:/demandes/" + demande.getId();
    }

    @GetMapping("/api/pieces")
    @ResponseBody
    public List<Map<String, Object>> getPieces(
            @RequestParam(required = false) String typeDemande,
            @RequestParam(required = false) String typeProfil) {
        try {
            DemandeTypeCode typeDemandeCode = typeDemande != null ? DemandeTypeCode.valueOf(typeDemande) : null;
            ProfilTypeCode profilTypeCode = typeProfil != null ? ProfilTypeCode.valueOf(typeProfil) : null;

            List<com.example.mvcjsp.model.PieceJustificative> pieces = enregistrementDemandeService.piecesPour(typeDemandeCode, profilTypeCode);

            return pieces.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("libelle", p.getLibelle());
                map.put("obligatoire", p.isObligatoire());
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/demandes/{id}")
    public String demandDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Demande demande = demandeRepository.findById(id)
                .orElse(null);

        if (demande == null) {
            redirectAttributes.addFlashAttribute("errors", List.of("Dossier introuvable"));
            return "redirect:/";
        }

        model.addAttribute("demande", demande);
        model.addAttribute("isScanned", scanService.isDemandeScanned(demande));
        model.addAttribute("scannedCount", scanService.getScannedPieceCount(id));
        model.addAttribute("totalCount", scanService.getTotalPieceCount(id));
        model.addAttribute("allPiecesScanned", scanService.areAllPiecesScanned(id));

        return "demande-detail";
    }

    @PostMapping("/demandes/{id}/marquer-scannee")
    @ResponseBody
    public Map<String, Object> markPieceAsScanned(@PathVariable Long id, @RequestParam Long pieceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            scanService.markPieceAsScanned(id, pieceId);
            long scannedCount = scanService.getScannedPieceCount(id);
            long totalCount = scanService.getTotalPieceCount(id);
            boolean allScanned = scanService.areAllPiecesScanned(id);

            response.put("success", true);
            response.put("scannedCount", scannedCount);
            response.put("totalCount", totalCount);
            response.put("allPiecesScanned", allScanned);
            response.put("message", "Pièce marquée comme scannée");
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }

    @PostMapping("/demandes/{id}/finaliser-scan")
    public String finalizeScanning(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Demande demande = scanService.finalizeScanning(id);
            redirectAttributes.addFlashAttribute("successMessage", "Scan finalisé. Dossier verrouillé.");
            return "redirect:/demandes/" + id;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errors", List.of(e.getMessage()));
            return "redirect:/demandes/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errors", List.of(e.getMessage()));
            return "redirect:/";
        }
    }

    @PostMapping("/demandes/{id}/upload-fichier")
    @ResponseBody
    public Map<String, Object> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam Long pieceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            FichierDossier fichier = fileUploadService.uploadFile(id, pieceId, file);
            long fileCount = fileUploadService.getFileCount(id);

            Map<String, Object> fichierMap = new HashMap<>();
            fichierMap.put("id", fichier.getId());
            fichierMap.put("nomFichier", fichier.getNomFichier());
            fichierMap.put("tailleFichier", fichier.getTailleFichier());
            fichierMap.put("dateUpload", fichier.getDateUpload());

            response.put("success", true);
            response.put("fichier", fichierMap);
            response.put("fileCount", fileCount);
            response.put("message", "Fichier téléchargé avec succès");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Erreur lors du téléchargement: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/demandes/{id}/fichiers")
    @ResponseBody
    public List<Map<String, Object>> listFiles(@PathVariable Long id) {
        List<FichierDossier> fichiers = fileUploadService.listFiles(id);
        return fichiers.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("nomFichier", f.getNomFichier());
            map.put("tailleFichier", f.getTailleFichier());
            map.put("dateUpload", f.getDateUpload());
            map.put("typeContenu", f.getTypeContenu());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/demandes/{demandeId}/piece/{pieceId}/fichiers")
    @ResponseBody
    public List<Map<String, Object>> listFilesForPiece(@PathVariable Long demandeId, @PathVariable Long pieceId) {
        List<FichierDossier> fichiers = fileUploadService.listFilesForPiece(demandeId, pieceId);
        return fichiers.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("nomFichier", f.getNomFichier());
            map.put("tailleFichier", f.getTailleFichier());
            map.put("dateUpload", f.getDateUpload());
            map.put("typeContenu", f.getTypeContenu());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/demandes/{demandeId}/fichiers/{fichierDossierId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long demandeId, @PathVariable Long fichierDossierId) {
        try {
            byte[] fileContent = fileUploadService.downloadFile(fichierDossierId);
            FichierDossier fichier = fileUploadService.getFileDetails(fichierDossierId);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fichier.getNomFichier() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length))
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/demandes/{demandeId}/fichiers/{fichierDossierId}")
    @ResponseBody
    public Map<String, Object> deleteFile(@PathVariable Long demandeId, @PathVariable Long fichierDossierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            fileUploadService.deleteFile(fichierDossierId);
            long fileCount = fileUploadService.getFileCount(demandeId);

            response.put("success", true);
            response.put("fileCount", fileCount);
            response.put("message", "Fichier supprimé avec succès");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return response;
    }
}

