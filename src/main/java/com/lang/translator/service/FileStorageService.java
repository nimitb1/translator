package com.lang.translator.service;

import com.lang.translator.dto.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStorageService implements IFileStorageService {

    private final String fileStoragePath;

    private static final int BUFFER_SIZE = 4096;

    @Autowired
    IUIResourceCompile resourceCompile;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws Exception {
        fileStoragePath = fileStorageProperties.getUploadDir();
        System.out.println("Creating the storage folders!");
        try {
            File root = new File(fileStoragePath);
            root.mkdirs();
        } catch (SecurityException e) {
            System.out.println("The system does not have the permission to create the directories");
            e.printStackTrace();
            System.exit(2);
        }
        System.out.println("completed!");
    }

    @Override
    public String storeFile(MultipartFile file) {
        String uniqueId = "";
        System.out.println("Starting the file storage");
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fullPath = fileStoragePath + "/" + fileName;
        File targetFile = new File(fullPath);
        OutputStream os = null;
        try {
            if(targetFile.exists()){
                throw new FileAlreadyExistsException("File already exists");
            }
            os = new FileOutputStream(targetFile);
            int read = 0;
            InputStream is= file.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            while((read = is.read(buffer)) != -1){
                os.write(buffer, 0, read);
            }
            os.flush();
            os.close();

            System.out.println("File Storage completed");
            System.out.println("Starting file unzipping");
            unzipFile(fileName);
        } catch (IOException e) {
            System.out.println("Error while storing the file");
            e.printStackTrace();
        }
        resourceCompile.initProcess(uniqueId, fullPath);
        return uniqueId;
    }

    private void unzipFile(String fileName) throws IOException {
        System.out.println("Starting file extraction ::"  + fileName);
        String extractionPath = fileStoragePath + "/" + fileName.split("\\.")[0];
        File filesDir = new File(extractionPath);
        if (!filesDir.exists()){
            filesDir.mkdirs();
        } else {
            filesDir.delete();
        }
        FileInputStream fis = new FileInputStream(fileStoragePath + "/" + fileName);
        ZipInputStream zipInputStream = new ZipInputStream(fis);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while(zipEntry != null){
            String filePath = extractionPath + "/" + zipEntry.getName();
            if(zipEntry.isDirectory()){
                System.out.println("Found a Dir");
                File dir = new File(filePath);
                dir.mkdir();
            } else {
                System.out.println("Found file, moving!");
                extractFile(zipInputStream, filePath);
                System.out.println("moving complete");
            }
            zipEntry = zipInputStream.getNextEntry();
        }
    }

    private void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipInputStream.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.flush();
        bos.close();
    }

}
