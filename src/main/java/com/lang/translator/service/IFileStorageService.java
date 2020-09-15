package com.lang.translator.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;

public interface IFileStorageService {

    String storeFile(MultipartFile file, String emailId);

    String getZipFile(String uniqueId);

    String getExcelFilePath(String uniqueId);

    default String generateUniqueId(String emailId, String fileName) {
        int strength = 5;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
        return bCryptPasswordEncoder.encode(emailId+fileName);
    }

}
