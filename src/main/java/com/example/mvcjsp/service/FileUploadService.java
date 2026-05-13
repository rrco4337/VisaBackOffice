package com.example.mvcjsp.service;

import com.example.mvcjsp.config.FileUploadConfig;
import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.DemandePiece;
import com.example.mvcjsp.model.FichierDossier;
import com.example.mvcjsp.model.enums.DemandeStatus;
import com.example.mvcjsp.model.enums.FichierCategorie;
import com.example.mvcjsp.repository.DemandeRepository;
import com.example.mvcjsp.repository.DemandePieceRepository;
import com.example.mvcjsp.repository.FichierDossierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileUploadService {
    @Autowired
    private FichierDossierRepository fichierDossierRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private DemandePieceRepository demandePieceRepository;

    @Autowired
    private FileUploadConfig fileUploadConfig;

    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String IMAGE_PNG_MIME_TYPE = "image/png";
    private static final String IMAGE_JPEG_MIME_TYPE = "image/jpeg";

    /**
     * Upload a file for a specific piece of a demande
     */
    public FichierDossier uploadFile(Long demandeId, Long pieceId, MultipartFile file) {
        // Validate demande exists and is not locked
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable: " + demandeId));

        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouillé. Les uploads ne sont pas autorisés.");
        }

        // Validate piece exists and is associated with demande
        DemandePiece demandePiece = demandePieceRepository.findByDemandeIdAndPieceId(demandeId, pieceId)
                .orElseThrow(() -> new IllegalArgumentException("Pièce non associée à ce dossier: " + pieceId));

        // Check if file already exists for this piece (prevent duplicates)
        if (fichierDossierRepository.existsByDemandeIdAndPieceId(demandeId, pieceId)) {
            throw new IllegalStateException("Un fichier existe déjà pour cette pièce. Supprimez l'ancien fichier avant d'en uploader un nouveau.");
        }

        // Validate file
        validatePdfFile(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = generateUniqueFilename(demandeId, "pdf");

        // Create date-based directory
        Path uploadDir = createDateBasedDirectory();

        // Save file to disk
        Path filePath = uploadDir.resolve(uniqueFilename);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier: " + e.getMessage(), e);
        }

        // Create database entry
        FichierDossier fichier = new FichierDossier();
        fichier.setDemande(demande);
        fichier.setPiece(demandePiece.getPiece());
        fichier.setCategorie(FichierCategorie.PIECE);
        fichier.setNomFichier(originalFilename);
        fichier.setCheminFichier(filePath.toString());
        fichier.setTailleFichier(file.getSize());
        fichier.setTypeContenu(file.getContentType());
        fichier.setDateUpload(LocalDateTime.now());
        fichier.setDateModification(LocalDateTime.now());

        return fichierDossierRepository.save(fichier);
    }

    /**
     * Upload a capture (photo or signature) for a demande
     */
    public FichierDossier uploadCapture(Long demandeId, MultipartFile file, FichierCategorie categorie) {
        if (categorie != FichierCategorie.PHOTO && categorie != FichierCategorie.SIGNATURE) {
            throw new IllegalArgumentException("Categorie de capture invalide");
        }

        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable: " + demandeId));

        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouille. Les captures ne sont pas autorisees.");
        }

        validateImageFile(file);

        if (categorie == FichierCategorie.SIGNATURE
            && fichierDossierRepository.existsByDemandeIdAndCategorie(demandeId, categorie)) {
            throw new IllegalStateException("La signature est deja enregistree");
        }

        if (categorie == FichierCategorie.PHOTO) {
            fichierDossierRepository.findByDemandeIdAndCategorie(demandeId, categorie)
                .ifPresent(existing -> deleteCaptureFile(existing));
        }

        String extension = contentTypeToExtension(file.getContentType());
        String uniqueFilename = generateUniqueFilename(demandeId, extension);
        Path uploadDir = createDateBasedDirectory();
        Path filePath = uploadDir.resolve(uniqueFilename);

        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier: " + e.getMessage(), e);
        }

        FichierDossier fichier = new FichierDossier();
        fichier.setDemande(demande);
        fichier.setPiece(null);
        fichier.setCategorie(categorie);
        fichier.setNomFichier(buildCaptureName(categorie, extension));
        fichier.setCheminFichier(filePath.toString());
        fichier.setTailleFichier(file.getSize());
        fichier.setTypeContenu(file.getContentType());
        fichier.setDateUpload(LocalDateTime.now());
        fichier.setDateModification(LocalDateTime.now());

        FichierDossier saved = fichierDossierRepository.save(fichier);
        updateCaptureStatus(demande, categorie, true);
        return saved;
    }

    public void deleteCapture(Long demandeId, FichierCategorie categorie) {
        if (categorie == FichierCategorie.SIGNATURE) {
            throw new IllegalStateException("La signature ne peut pas etre supprimee");
        }
        FichierDossier fichier = fichierDossierRepository.findByDemandeIdAndCategorie(demandeId, categorie)
                .orElseThrow(() -> new IllegalArgumentException("Capture introuvable"));

        Demande demande = fichier.getDemande();
        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouille. La suppression n'est pas autorisee.");
        }

        deleteCaptureFile(fichier);
        updateCaptureStatus(demande, categorie, false);
    }

    public FichierDossier getCapture(Long demandeId, FichierCategorie categorie) {
        return fichierDossierRepository.findByDemandeIdAndCategorie(demandeId, categorie).orElse(null);
    }

    /**
     * Delete a file
     */
    public void deleteFile(Long fichierDossierId) {
        FichierDossier fichier = fichierDossierRepository.findById(fichierDossierId)
                .orElseThrow(() -> new IllegalArgumentException("Fichier introuvable: " + fichierDossierId));

        Demande demande = fichier.getDemande();
        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouillé. La suppression n'est pas autorisée.");
        }

        // Delete from disk
        try {
            Files.deleteIfExists(Paths.get(fichier.getCheminFichier()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier: " + e.getMessage(), e);
        }

        // Delete from database
        fichierDossierRepository.delete(fichier);
    }

    /**
     * List all files for a demande
     */
    public List<FichierDossier> listFiles(Long demandeId) {
        return fichierDossierRepository.findByDemandeIdOrderByDateUploadDesc(demandeId);
    }

    /**
     * List files for a specific piece of a demande
     */
    public List<FichierDossier> listFilesForPiece(Long demandeId, Long pieceId) {
        return fichierDossierRepository.findByDemandeIdAndPieceIdOrderByDateUploadDesc(demandeId, pieceId);
    }

    /**
     * Get file details
     */
    public FichierDossier getFileDetails(Long fichierDossierId) {
        FichierDossier fichier = fichierDossierRepository.findById(fichierDossierId)
                .orElseThrow(() -> new IllegalArgumentException("Fichier introuvable: " + fichierDossierId));

        // Verify file exists on disk
        if (!Files.exists(Paths.get(fichier.getCheminFichier()))) {
            throw new RuntimeException("Le fichier n'existe pas sur le disque: " + fichier.getCheminFichier());
        }

        return fichier;
    }

    /**
     * Get file content for download
     */
    public byte[] downloadFile(Long fichierDossierId) throws IOException {
        FichierDossier fichier = getFileDetails(fichierDossierId);
        Path filePath = Paths.get(fichier.getCheminFichier());
        return Files.readAllBytes(filePath);
    }

    /**
     * Validate uploaded file
     */
    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        // Check MIME type
        if (!PDF_MIME_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés");
        }

        // Check file size
        long maxSize = fileUploadConfig.getMaxFileSizeInBytes();
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("La taille du fichier dépasse la limite: %d bytes max", maxSize)
            );
        }

        // Additional check: ensure filename is provided
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Le nom du fichier est invalide");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (!IMAGE_PNG_MIME_TYPE.equals(contentType) && !IMAGE_JPEG_MIME_TYPE.equals(contentType)) {
            throw new IllegalArgumentException("Seules les images PNG/JPEG sont autorisees");
        }

        long maxSize = fileUploadConfig.getMaxFileSizeInBytes();
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    String.format("La taille du fichier depasse la limite: %d bytes max", maxSize)
            );
        }
    }

    /**
     * Generate unique filename: demande-{id}-{uuid}.pdf
     */
    private String generateUniqueFilename(Long demandeId, String extension) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("demande-%d-%s.%s", demandeId, uuid, extension);
    }

    private String contentTypeToExtension(String contentType) {
        if (IMAGE_PNG_MIME_TYPE.equals(contentType)) {
            return "png";
        }
        if (IMAGE_JPEG_MIME_TYPE.equals(contentType)) {
            return "jpg";
        }
        return "bin";
    }

    private String buildCaptureName(FichierCategorie categorie, String extension) {
        return String.format("%s.%s", categorie.name().toLowerCase(), extension);
    }

    private void deleteCaptureFile(FichierDossier fichier) {
        try {
            Files.deleteIfExists(Paths.get(fichier.getCheminFichier()));
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression du fichier: " + e.getMessage(), e);
        }
        fichierDossierRepository.delete(fichier);
    }

    private void updateCaptureStatus(Demande demande, FichierCategorie categorie, boolean done) {
        if (categorie == FichierCategorie.PHOTO) {
            demande.setPhotoScanned(done);
        } else if (categorie == FichierCategorie.SIGNATURE) {
            demande.setSignatureScanned(done);
        }
        demandeRepository.save(demande);
    }

    /**
     * Create or get date-based upload directory
     */
    private Path createDateBasedDirectory() {
        LocalDate today = LocalDate.now();
        String dateDir = today.toString(); // YYYY-MM-DD format

        Path uploadDir = Paths.get(fileUploadConfig.getDir()).resolve(dateDir);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la création du répertoire: " + e.getMessage(), e);
        }

        return uploadDir;
    }

    /**
     * Get count of files for a demande
     */
    public long getFileCount(Long demandeId) {
        return fichierDossierRepository.countByDemandeId(demandeId);
    }
}
