package org.week6lap.week5photogallery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.week6lap.week5photogallery.model.Image;
import org.week6lap.week5photogallery.repository.ImageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;

    public Image saveImage(String objectKey, String description, String presignedUrl) {
        Image image = new Image();
        image.setObjectKey(objectKey);
        image.setDescription(description);
        image.setPresignedUrl(presignedUrl);
        return imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        // Refresh presigned URLs if needed
        List<Image> images = imageRepository.findByOrderByCreatedAtDesc();

        return images.stream().map(image -> {
            if (image.getUrlExpiresAt() == null ||
                    image.getUrlExpiresAt().isBefore(LocalDateTime.now().plusHours(1))) {
                // Regenerate URL if expiring within 1 hour
                String newUrl = s3Service.generatePresignedViewUrl(image.getObjectKey());
                image.setPresignedUrl(newUrl);
                image.setUrlExpiresAt(LocalDateTime.now().plusDays(2));
                imageRepository.save(image);
            }
            return image;
        }).collect(Collectors.toList());
    }
}