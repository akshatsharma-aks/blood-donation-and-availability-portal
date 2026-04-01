package com.blooddonation.service;

import com.blooddonation.exception.BusinessException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.UUID;
import java.util.Objects;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) {

        try {

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

            Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + "_"
                    + StringUtils.cleanPath(
                    Objects.requireNonNull(file.getOriginalFilename())
            );

            Path target = uploadPath.resolve(filename);

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/" + filename;

        } catch (IOException ex) {

            throw new BusinessException(
                    "Failed to store file: " + ex.getMessage()
            );
        }
    }
}