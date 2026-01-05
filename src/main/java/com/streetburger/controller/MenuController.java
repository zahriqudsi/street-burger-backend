package com.streetburger.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.MenuCategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.model.MenuItem;
import com.streetburger.repository.MenuCategoryRepository;
import com.streetburger.repository.MenuItemRepository;

@RestController
@RequestMapping("/menu")
@Tag(name = "Menu", description = "Menu categories and items")
public class MenuController {

    @Autowired
    private MenuCategoryRepository categoryRepository;

    @Autowired
    private MenuItemRepository itemRepository;

    // Category endpoints
    @GetMapping("/categories")
    @Operation(summary = "Get all menu categories")
    public ResponseEntity<ApiResponse<List<MenuCategory>>> getAllCategories() {
        List<MenuCategory> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping("/categories")
    @Operation(summary = "Add new category (Admin only)")
    public ResponseEntity<ApiResponse<MenuCategory>> addCategory(@RequestBody MenuCategory category) {
        MenuCategory saved = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", saved));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category (Admin only)")
    public ResponseEntity<ApiResponse<MenuCategory>> updateCategory(
            @PathVariable Long id,
            @RequestBody MenuCategory category) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    existing.setNameSi(category.getNameSi());
                    existing.setNameTa(category.getNameTa());
                    existing.setDisplayOrder(category.getDisplayOrder());
                    existing.setImageUrl(category.getImageUrl());
                    categoryRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Category updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Category not found")));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category (Admin only)")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Category not found"));
    }

    // Item endpoints
    @GetMapping("/items")
    @Operation(summary = "Get all menu items")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getAllItems() {
        List<MenuItem> items = itemRepository.findByIsAvailableTrue();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/items/{categoryId}")
    @Operation(summary = "Get items by category")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getItemsByCategory(@PathVariable Long categoryId) {
        List<MenuItem> items = itemRepository.findByCategoryIdAndIsAvailableTrue(categoryId);
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @GetMapping("/items/popular")
    @Operation(summary = "Get popular items")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getPopularItems() {
        List<MenuItem> items = itemRepository.findByIsPopularTrueAndIsAvailableTrue();
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping("/items")
    @Operation(summary = "Add new item (Admin only)")
    public ResponseEntity<ApiResponse<MenuItem>> addItem(@RequestBody MenuItem item) {
        MenuItem saved = itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created", saved));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update item (Admin only)")
    public ResponseEntity<ApiResponse<MenuItem>> updateItem(
            @PathVariable Long id,
            @RequestBody MenuItem item) {
        return itemRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(item.getTitle());
                    existing.setTitleSi(item.getTitleSi());
                    existing.setTitleTa(item.getTitleTa());
                    existing.setDescription(item.getDescription());
                    existing.setDescriptionSi(item.getDescriptionSi());
                    existing.setDescriptionTa(item.getDescriptionTa());
                    existing.setPrice(item.getPrice());
                    existing.setImageUrl(item.getImageUrl());
                    existing.setIsAvailable(item.getIsAvailable());
                    existing.setIsPopular(item.getIsPopular());
                    existing.setDisplayOrder(item.getDisplayOrder());
                    existing.setCategory(item.getCategory());
                    itemRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Item updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Item not found")));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Delete item (Admin only)")
    public ResponseEntity<ApiResponse<String>> deleteItem(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Item deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Item not found"));
    }
}
