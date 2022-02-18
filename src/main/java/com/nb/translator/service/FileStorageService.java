package com.nb.translator.service;

import com.nb.translator.dto.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class FileStorageService implements IFileStorageService {

    private final String fileStoragePath;

    private static final int BUFFER_SIZE = 4096;

    IUIResourceCompile resourceCompile;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws Exception {
        fileStoragePath = fileStorageProperties.getUploadDir();
        log.info("Creating the storage folders!");
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
    public String storeFile(MultipartFile file, String emailId) {
        resourceCompile = new UIResourceCompile();
        resourceCompile.setBaseFilePath(fileStoragePath);
        String uniqueId = generateUniqueId();
        log.info("Starting the file storage for fileId: " + uniqueId);
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fullPath = fileStoragePath + "/" + fileName;
        fileSave(file, fullPath);
        try {
            unzipFile(null, fileName);
        } catch (IOException e) {
            log.error("Error occurred while unzipping the file, {} for {}", fileName, emailId, e);
            e.printStackTrace();
        }
        resourceCompile.initProcess(uniqueId, fullPath);
        return uniqueId;
    }

    private void fileSave(MultipartFile file, String fullPath) {
        log.info("Initiating the file storage");
        File targetFile = new File(fullPath);
        OutputStream os = null;
        try {
            if(targetFile.exists()){
                log.error("File already exists with the same name");
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

            log.info("File Storage completed");
            log.info("Starting file unzipping");
        } catch (IOException e) {
            log.error("Error while storing the file", e);
            e.printStackTrace();
        }
    }

    @Override
    public String getZipFile(String uniqueId) {
        return fileStoragePath + "/" + uniqueId + ".zip";
    }

    @Override
    public String getExcelFilePath(String uniqueId) {
        return fileStoragePath + "/" + uniqueId + ".xlsx";
    }

    @Override
    public String storeFilesForTranslation(MultipartFile zipFile, MultipartFile excelFile, String emailId) {
        String uniqueId = generateUniqueId();
        resourceCompile = new UIResourceCompile();
        String translateFileStoragePath = fileStoragePath + "/" + uniqueId;
        resourceCompile.setBaseFilePath(translateFileStoragePath);
        log.info("Setting the file path to {}", translateFileStoragePath );
        String zipFileName = StringUtils.cleanPath(zipFile.getOriginalFilename());
        String excelFileName = StringUtils.cleanPath(excelFile.getOriginalFilename());

        String fullZipFilePath = translateFileStoragePath + "/" + zipFileName;
        String fullExcelFilePath = translateFileStoragePath + "/" + excelFileName;
        fileSave(zipFile, fullZipFilePath);
        fileSave(excelFile, fullExcelFilePath);

        try {
            unzipFile(translateFileStoragePath, zipFileName);
        } catch (IOException e) {
            log.error("Error while extracting the file {} for {}", zipFile, emailId, e);
            e.printStackTrace();
        }

        //TODO: Code to put the translated values into the files. Get the translation language value from the user.

        return uniqueId;
    }

    private void unzipFile(String storePath, String fileName) throws IOException {
        log.info("Starting file extraction ::"  + fileName);
        int dirCount = 0;
        int fileCount = 0;
        String extractionPath = storePath == null?fileStoragePath:storePath + "/" + fileName.split("\\.")[0];
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
                ++dirCount;
                File dir = new File(filePath);
                dir.mkdir();
            } else {
                ++fileCount;
                extractFile(zipInputStream, filePath);
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        log.info("Extraction Complete. Total extracted folders = {}, files = {}", dirCount, fileCount);
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
