package com.example.demo.controller;

import com.example.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload/event-image")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> uploadEventImage(@RequestParam("file") MultipartFile file){
        String fileUrl = fileStorageService.storeFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("message", "File uploaded successfully");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/event-images/{filename:.+}")
    public ResponseEntity<Resource> getEventImage(@PathVariable String filename){
        try {
            Path filePath = fileStorageService.loadFile(filename);
            Resource resource=new UrlResource(filePath.toUri());

            if(resource.exists() && resource.isReadable()){
                String contentType="application/octet-stream";
                try{
                    contentType=java.nio.file.Files.probeContentType(filePath);
                }catch(IOException ex){

                }
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\""+resource.getFilename()+"\"")
                .body(resource);
            }else {
                throw new RuntimeException("File not found: "+ filename);
            }
        }catch (Exception ex){
            throw new RuntimeException("Error reading file "+filename, ex);
        }
    }
}
