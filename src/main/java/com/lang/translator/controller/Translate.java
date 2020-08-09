package com.lang.translator.controller;

import com.lang.translator.service.IFileStorageService;
import com.lang.translator.service.IUIResourceCompile;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;

//TODO: add the email validation
@RestController
@RequestMapping("/translate")
@Slf4j
public class Translate {

    @Autowired
    IUIResourceCompile uiCompile;

    @Autowired
    IFileStorageService storageService;

    /**
     * Method to upload the zip file containing the keys and values for converting the same into Excel file
     */
    @PostMapping("/upload/zip")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file, @RequestParam("emailId") String emailId) {
        log.info("Received the file>>" + file.getName() + "--" + file.getContentType());
        return ResponseEntity.ok("{'msg':'File uploaded.','ReferenceNo':' "+ storageService.storeFile(file, emailId) + "}");
    }

    /**
     * Method to fetch the converted the zip file.
     */
    @GetMapping(value="/download/zip", produces = "application/zip")
    public void getZipFile(@RequestParam("uniqueId") String uniqueId) {

    }

    /**
     * Method to get the excel file to containing the keys and values.
     */
    @GetMapping(value = "/download/excel/{uniqueId}", produces = "application/zip")
    public void getExcelFile(@PathParam("uniqueId") String uniqueId, @RequestParam("emailId") String emailId) {

    }

}
