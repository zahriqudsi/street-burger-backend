package com.streetburger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.model.Chef;
import com.streetburger.repository.ChefRepository;

@RestController
@RequestMapping("/chefs")
@Tag(name = "Chefs", description = "Restaurant chefs")
public class ChefController {

    @Autowired
    private ChefRepository chefRepository;

    @GetMapping
    @Operation(summary = "Get all chefs")
    public ResponseEntity<ApiResponse<List<Chef>>> getAll() {
        List<Chef> chefs = chefRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        return ResponseEntity.ok(ApiResponse.success(chefs));
    }

    @PostMapping
    @Operation(summary = "Add chef (Admin)")
    public ResponseEntity<ApiResponse<Chef>> add(@RequestBody Chef chef) {
        Chef saved = chefRepository.save(chef);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Chef added", saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update chef (Admin)")
    public ResponseEntity<ApiResponse<Chef>> update(
            @PathVariable Long id,
            @RequestBody Chef chef) {

        return chefRepository.findById(id)
                .map(existing -> {
                    if (chef.getName() != null)
                        existing.setName(chef.getName());
                    if (chef.getTitle() != null)
                        existing.setTitle(chef.getTitle());
                    if (chef.getBio() != null)
                        existing.setBio(chef.getBio());
                    if (chef.getImageUrl() != null)
                        existing.setImageUrl(chef.getImageUrl());
                    if (chef.getDisplayOrder() != null)
                        existing.setDisplayOrder(chef.getDisplayOrder());
                    if (chef.getIsActive() != null)
                        existing.setIsActive(chef.getIsActive());

                    chefRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Chef updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Chef not found")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete chef (Admin)")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (chefRepository.existsById(id)) {
            chefRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Chef deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Chef not found"));
    }
}
