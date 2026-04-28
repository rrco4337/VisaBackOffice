package com.example.mvcjsp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class FileUploadConfig {
    private String dir = "./uploads";
    private String maxFileSize = "10MB";
    private int maxFilesPerDemande = 10;

    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }

    public String getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(String maxFileSize) { this.maxFileSize = maxFileSize; }

    public int getMaxFilesPerDemande() { return maxFilesPerDemande; }
    public void setMaxFilesPerDemande(int maxFilesPerDemande) { this.maxFilesPerDemande = maxFilesPerDemande; }

    /**
     * Convert human-readable size (e.g., "10MB") to bytes
     */
    public long getMaxFileSizeInBytes() {
        return parseSize(maxFileSize);
    }

    /**
     * Parse size strings like "10MB", "1GB", etc. to bytes
     */
    public static long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            return 10 * 1024 * 1024; // Default 10MB
        }

        String upperSize = sizeStr.toUpperCase().trim();
        long multiplier = 1;

        if (upperSize.endsWith("KB")) {
            multiplier = 1024;
            upperSize = upperSize.substring(0, upperSize.length() - 2);
        } else if (upperSize.endsWith("MB")) {
            multiplier = 1024 * 1024;
            upperSize = upperSize.substring(0, upperSize.length() - 2);
        } else if (upperSize.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            upperSize = upperSize.substring(0, upperSize.length() - 2);
        }

        try {
            return Long.parseLong(upperSize.trim()) * multiplier;
        } catch (NumberFormatException e) {
            return 10 * 1024 * 1024; // Default 10MB
        }
    }

    /**
     * Get the upload directory path, ensuring it exists
     */
    public java.nio.file.Path getUploadPath() {
        java.nio.file.Path path = Paths.get(dir);
        try {
            java.nio.file.Files.createDirectories(path);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Could not create upload directory: " + dir, e);
        }
        return path;
    }
}
