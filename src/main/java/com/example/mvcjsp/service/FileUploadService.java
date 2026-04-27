package com.example.mvcjsp.service;

import com.example.mvcjsp.config.FileUploadConfig;
import com.example.mvcjsp.model.Demande;
import com.example.mvcjsp.model.FichierDossier;
import com.example.mvcjsp.model.enums.DemandeStatus;
import com.example.mvcjsp.repository.DemandeRepository;
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
    private FileUploadConfig fileUploadConfig;

    private static final String PDF_MIME_TYPE = "application/pdf";

    /**
     * Upload a file for a demande
     */
    public FichierDossier uploadFile(Long demandeId, MultipartFile file) {
        // Validate demande exists and is not locked
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable: " + demandeId));

        if (demande.getStatut() == DemandeStatus.SCANNE) {
            throw new IllegalStateException("Le dossier est verrouillé. Les uploads ne sont pas autorisés.");
        }

        // Validate file
        validateFile(file);

        // Check file count limit
        long fileCount = fichierDossierRepository.countByDemandeId(demandeId);
        if (fileCount >= fileUploadConfig.getMaxFilesPerDemande()) {
            throw new IllegalStateException(
                    String.format("Nombre maximum de fichiers atteint (%d)", fileUploadConfig.getMaxFilesPerDemande())
            );
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String sanitizedName = sanitizeFilename(originalFilename);
        String uniqueFilename = generateUniqueFilename(demandeId, sanitizedName);

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
        fichier.setNomFichier(originalFilename);
        fichier.setCheminFichier(filePath.toString());
        fichier.setTailleFichier(file.getSize());
        fichier.setTypeContenu(file.getContentType());
        fichier.setDateUpload(LocalDateTime.now());
        fichier.setDateModification(LocalDateTime.now());
        // Note: utilisateurUpload could be set from authentication context if available

        return fichierDossierRepository.save(fichier);
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
    private void validateFile(MultipartFile file) {
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

    /**
     * Sanitize filename to remove dangerous characters
     */
    private String sanitizeFilename(String filename) {
        return filename
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }

    /**
     * Generate unique filename: demande-{id}-{uuid}.pdf
     */
    private String generateUniqueFilename(Long demandeId, String originalFilename) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("demande-%d-%s.pdf", demandeId, uuid);
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
