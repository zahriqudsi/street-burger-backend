package com.streetburger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.model.GalleryImage;
import com.streetburger.repository.GalleryImageRepository;

@RestController
@RequestMapping("/gallery")
@Tag(name = "Gallery", description = "Restaurant gallery images")
public class GalleryController {

    @Autowired
    private GalleryImageRepository galleryImageRepository;

    @GetMapping
    @Operation(summary = "Get all gallery images")
    public ResponseEntity<ApiResponse<List<GalleryImage>>> getAll() {
        List<GalleryImage> images = galleryImageRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        return ResponseEntity.ok(ApiResponse.success(images));
    }

    @PostMapping
    @Operation(summary = "Add gallery image (Admin)")
    public ResponseEntity<ApiResponse<GalleryImage>> add(@RequestBody GalleryImage image) {
        GalleryImage saved = galleryImageRepository.save(image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update gallery image (Admin)")
    public ResponseEntity<ApiResponse<GalleryImage>> update(
            @PathVariable Long id,
            @RequestBody GalleryImage image) {

        return galleryImageRepository.findById(id)
                .map(existing -> {
                    if (image.getImageUrl() != null)
                        existing.setImageUrl(image.getImageUrl());
                    if (image.getCaption() != null)
                        existing.setCaption(image.getCaption());
                    if (image.getDisplayOrder() != null)
                        existing.setDisplayOrder(image.getDisplayOrder());
                    if (image.getIsActive() != null)
                        existing.setIsActive(image.getIsActive());

                    galleryImageRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Image updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Image not found")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete gallery image (Admin)")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (galleryImageRepository.existsById(id)) {
            galleryImageRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Image deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Image not found"));
    }
}
