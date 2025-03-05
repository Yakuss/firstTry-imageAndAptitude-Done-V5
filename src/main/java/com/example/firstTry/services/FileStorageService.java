package com.example.firstTry.services;

import com.example.firstTry.exception.FileStorageException;
import com.example.firstTry.exception.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(@Value("${file.upload.dir}") String uploadDir) {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            logger.info("Using upload directory: {}", this.fileStorageLocation);
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            logger.error("Failed to create upload directory", ex);
            throw new RuntimeException("Could not create upload directory: " + uploadDir, ex);
        } catch (InvalidPathException ex) {
            throw new RuntimeException("Invalid upload directory path: " + uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file, String fileType, Long doctorId) throws IOException {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = "doctor_" + doctorId + "_" + fileType + "_" + UUID.randomUUID() + extension;
            Path targetLocation = fileStorageLocation.resolve(filename);

            logger.info("Storing file to: {}", targetLocation);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully stored: {}", filename);

            return filename;
        } catch (IOException ex) {
            logger.error("Failed to store file", ex);
            throw ex;
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }

    public byte[] loadFile(String filename) throws IOException {
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            throw new StorageFileNotFoundException("File not found: " + filename);
        }
        return Files.readAllBytes(filePath);
    }

    public void deleteFile(String filename) throws IOException {
        if (filename != null) {
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        }
    }
}
