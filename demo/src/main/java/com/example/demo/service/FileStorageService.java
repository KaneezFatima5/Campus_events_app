package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private final String baseURL;
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir, @Value("${file.base-url}") String baseURL){
        this.baseURL=baseURL;
        this.fileStorageLocation= Paths.get(uploadDir).toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception ex){
            throw new RuntimeException("Could not create upload directory!", ex);
        }
    }

    public String storeFile(MultipartFile file){
        if(file.isEmpty()){
            throw new RuntimeException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if(contentType==null || !contentType.startsWith("image/")){
            throw new RuntimeException("Only image files are allowed");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension ="";
        if(originalFileName.contains(".")){
            fileExtension=originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString()+fileExtension;
        try{
            if(uniqueFileName.contains("..")){
                throw new RuntimeException("Invalid FileName "+uniqueFileName);
            }
            Path targetLocation =this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return baseURL+"/api/v1/files/event-images/"+uniqueFileName;
        }catch (IOException ex){
            throw new RuntimeException("Could not store file "+ uniqueFileName, ex);
        }
    }

    public void deleteFile(String fileUrl){
        try{
            String fileName =fileUrl.substring(fileUrl.lastIndexOf("/")+1);
            Path filePath =this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        }catch (IOException ex){
            throw new RuntimeException("Could not delete file"+ex);
        }
    }
    public Path loadFile(String fileName){
        return fileStorageLocation.resolve(fileName).normalize();
    }

}
