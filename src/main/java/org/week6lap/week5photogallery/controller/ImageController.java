package org.week6lap.week5photogallery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.week6lap.week5photogallery.service.ImageService;
import org.week6lap.week5photogallery.service.S3Service;

import java.util.List;

@RestController
public class ImageController {

    private final ImageService imageService;
    private final S3Service s3Service;

    public ImageController(ImageService imageService, S3Service s3Service) {
        this.imageService = imageService;
        this.s3Service = s3Service;
    }

    // JSON endpoint for gallery
    @GetMapping("/images")
    public List<ImageResponse> getImages() {
        return imageService.getAllImages().stream()
                .map(img -> new ImageResponse(
                        img.getId(),
                        img.getDescription(),
                        s3Service.generatePresignedViewUrl(img.getObjectKey()),
                        img.getCreatedAt()
                ))
                .toList();
    }

    // DTO for JSON response
    public record ImageResponse(Long id, String description, String url, java.time.LocalDateTime createdAt) {}
}
