package com.lang.translator.controller;

import com.lang.translator.service.IFileStorageService;
import com.lang.translator.service.IUIResourceCompile;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

@RestController
@RequestMapping("/translate")
@Slf4j
public class Translate {

    @Autowired
    IFileStorageService storageService;

    /**
     * Method to upload the zip file containing the keys and values for converting the same into Excel file
     */
    @PostMapping("/upload/zip")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file, @RequestParam("emailId") String emailId) {
        log.info("Received the file>>" + file.getName() + "--" + file.getContentType());
        return ResponseEntity.ok("{'msg':'File uploaded.','ReferenceNo':'"+ storageService.storeFile(file, emailId) + "'}");
    }

    /**
     * Method to get the excel file to containing the keys and values.
     */
    @GetMapping(value = "/download/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public FileSystemResource getExcelFile(@RequestParam("uniqueId") String uniqueId, @RequestParam("emailId") String emailId) {
        log.info("Received the request to fetch the file: {}", uniqueId);
        return new FileSystemResource(storageService.getExcelFilePath(uniqueId));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFileForTranslation(@RequestParam("zip")MultipartFile zipFile, @RequestParam("translate")MultipartFile translate, @RequestParam("emailId") String emailId) {
        log.info("Received the request to translate the file: {}", emailId);
        return ResponseEntity.ok("{'msg':'Files have been uploaded.','ReferenceNo':'"
                                        + storageService.storeFilesForTranslation(zipFile, translate, emailId) + "'}");
    }

}
