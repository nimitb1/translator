package com.nb.translator.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.UUID;

public interface IFileStorageService {

    /**
     * Method to store the file and the parse the contents to the excel file.
     * @param file
     * @param emailId
     * @return
     */
    String storeFile(MultipartFile file, String emailId);

    String getZipFile(String uniqueId);

    String getExcelFilePath(String uniqueId);

    String storeFilesForTranslation(MultipartFile zipFile, MultipartFile excelFile, String emailId);

    default String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

}
