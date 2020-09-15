package com.lang.translator.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.UUID;

public interface IFileStorageService {

    String storeFile(MultipartFile file, String emailId);

    String getZipFile(String uniqueId);

    String getExcelFilePath(String uniqueId);

    default String generateUniqueId(String emailId, String fileName) {
        return UUID.randomUUID().toString();
    }

}
