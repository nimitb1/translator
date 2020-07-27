package com.lang.translator.controller;

import com.lang.translator.service.IFileStorageService;
import com.lang.translator.service.IUIResourceCompile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/translate")
public class Translate {

    @Autowired
    IUIResourceCompile uiCompile;

    @Autowired
    IFileStorageService storageService;

    @PostMapping("/upload/zip")
    public ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file){
        System.out.println("Received the file>>" + file.getName() + "--" + file.getContentType());
        return ResponseEntity.ok("{'msg':'File uploaded.','ReferenceNo':' "+ storageService.storeFile(file) + "}");
    }
}
