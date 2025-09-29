package org.week6lap.week5photogallery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.week6lap.week5photogallery.model.Image;
import org.week6lap.week5photogallery.service.ImageService;
import org.week6lap.week5photogallery.service.S3Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class GalleryController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private S3Service s3Service;

    @GetMapping("/")
    public String gallery(Model model) {
        model.addAttribute("images", imageService.getAllImages());
        model.addAttribute("image", new Image());
        return "gallery";
    }

    // Upload file + description
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("description") String description) {
        try {
            // Generate a unique S3 object key
            String objectKey = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Upload file to S3
            s3Service.uploadFile(file, objectKey);

            // Generate a presigned **view** URL for secure access
            String presignedUrl = s3Service.generatePresignedViewUrl(objectKey);

            // Save metadata to DB
            Image savedImage = imageService.saveImage(objectKey, description, presignedUrl);

            // Build JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedImage.getId());
            response.put("objectKey", objectKey);
            response.put("description", description);
            response.put("url", presignedUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}