package org.week6lap.week5photogallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.week6lap.week5photogallery.model.Image;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE i.urlExpiresAt > :now OR i.urlExpiresAt IS NULL")
    List<Image> findAllWithValidUrls(LocalDateTime now);

    List<Image> findByOrderByCreatedAtDesc();
}