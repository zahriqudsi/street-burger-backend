package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.model.RestaurantInfo;
import com.streetburger.repository.RestaurantInfoRepository;

@RestController
@RequestMapping("/restaurant-info")
@Tag(name = "Restaurant Info", description = "Restaurant information management")
public class RestaurantInfoController {

    @Autowired
    private RestaurantInfoRepository restaurantInfoRepository;

    @PostMapping("/add")
    @Operation(summary = "Add restaurant info (Admin)")
    public ResponseEntity<ApiResponse<RestaurantInfo>> add(@RequestBody RestaurantInfo info) {
        info.setUpdatedAt(LocalDateTime.now());
        RestaurantInfo saved = restaurantInfoRepository.save(info);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Restaurant info added", saved));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update restaurant info (Admin)")
    public ResponseEntity<ApiResponse<RestaurantInfo>> update(
            @PathVariable Long id,
            @RequestBody RestaurantInfo info) {

        return restaurantInfoRepository.findById(id)
                .map(existing -> {
                    if (info.getName() != null)
                        existing.setName(info.getName());
                    if (info.getAddress() != null)
                        existing.setAddress(info.getAddress());
                    if (info.getPhone() != null)
                        existing.setPhone(info.getPhone());
                    if (info.getEmail() != null)
                        existing.setEmail(info.getEmail());
                    if (info.getOpeningHours() != null)
                        existing.setOpeningHours(info.getOpeningHours());
                    if (info.getAboutUs() != null)
                        existing.setAboutUs(info.getAboutUs());
                    if (info.getLatitude() != null)
                        existing.setLatitude(info.getLatitude());
                    if (info.getLongitude() != null)
                        existing.setLongitude(info.getLongitude());
                    if (info.getFacebookUrl() != null)
                        existing.setFacebookUrl(info.getFacebookUrl());
                    if (info.getInstagramUrl() != null)
                        existing.setInstagramUrl(info.getInstagramUrl());
                    if (info.getUberEatsUrl() != null)
                        existing.setUberEatsUrl(info.getUberEatsUrl());
                    if (info.getPickmeFoodUrl() != null)
                        existing.setPickmeFoodUrl(info.getPickmeFoodUrl());
                    existing.setUpdatedAt(LocalDateTime.now());

                    restaurantInfoRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Restaurant info updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Restaurant info not found")));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get restaurant info by ID")
    public ResponseEntity<ApiResponse<RestaurantInfo>> getById(@PathVariable Long id) {
        return restaurantInfoRepository.findById(id)
                .map(info -> ResponseEntity.ok(ApiResponse.success(info)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Restaurant info not found")));
    }

    @GetMapping("/get/all")
    @Operation(summary = "Get all restaurant info")
    public ResponseEntity<ApiResponse<List<RestaurantInfo>>> getAll() {
        List<RestaurantInfo> infoList = restaurantInfoRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(infoList));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete restaurant info (Admin)")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        if (restaurantInfoRepository.existsById(id)) {
            restaurantInfoRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Restaurant info deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Restaurant info not found"));
    }
}
