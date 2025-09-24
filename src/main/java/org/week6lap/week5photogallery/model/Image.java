package org.week6lap.week5photogallery.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String objectKey;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 2048)
    private String presignedUrl;

    private LocalDateTime createdAt;

    private LocalDateTime urlExpiresAt;

    public Image() {
        this.createdAt = LocalDateTime.now();
    }

    public Image(String objectKey, String description) {
        this();
        this.objectKey = objectKey;
        this.description = description;
    }
}
