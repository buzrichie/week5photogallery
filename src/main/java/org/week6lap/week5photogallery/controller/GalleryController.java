package org.week6lap.week5photogallery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.week6lap.week5photogallery.model.Image;
import org.week6lap.week5photogallery.service.ImageService;
import org.week6lap.week5photogallery.service.S3Service;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/upload")
    public ResponseEntity<?> prepareUpload(@RequestParam String description,
                                           @RequestParam String fileName) {
        try {
            // Generate presigned URL for upload
            String result = s3Service.generatePresignedUploadUrl(fileName, "image/jpeg");
            String[] parts = result.split("\\|");
            String objectKey = parts[0];
            String uploadUrl = parts[1];

            // Save image metadata in DB
            Image image = imageService.saveImage(objectKey, description);

            // Build JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("objectKey", objectKey);
            response.put("uploadUrl", uploadUrl);
            response.put("imageId", image.getId());
            response.put("message", "Upload prepared successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error preparing upload: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}