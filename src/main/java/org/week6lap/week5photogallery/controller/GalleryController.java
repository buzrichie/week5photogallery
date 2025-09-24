package org.week6lap.week5photogallery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.week6lap.week5photogallery.model.Image;
import org.week6lap.week5photogallery.service.ImageService;
import org.week6lap.week5photogallery.service.S3Service;

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
    public String prepareUpload(@RequestParam String description,
                                @RequestParam String fileName,
                                RedirectAttributes redirectAttributes) {
        try {
            // Generate presigned URL for upload
            String result = s3Service.generatePresignedUploadUrl(fileName, "image/jpeg");
            String[] parts = result.split("\\|");
            String objectKey = parts[0];
            String uploadUrl = parts[1];

            // Save image metadata
            Image image = imageService.saveImage(objectKey, description);

            redirectAttributes.addFlashAttribute("uploadUrl", uploadUrl);
            redirectAttributes.addFlashAttribute("objectKey", objectKey);
            redirectAttributes.addFlashAttribute("message", "Upload prepared successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error preparing upload: " + e.getMessage());
        }

        return "redirect:/";
    }
}