package com.streetburger.model;

import java.math.BigDecimal;

import com.streetburger.model.MenuCategory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "title_si", length = 150)
    private String titleSi; // Sinhala

    @Column(name = "title_ta", length = 150)
    private String titleTa; // Tamil

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "description_si", columnDefinition = "TEXT")
    private String descriptionSi;

    @Column(name = "description_ta", columnDefinition = "TEXT")
    private String descriptionTa;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "is_popular")
    private Boolean isPopular = false;

    @Column(name = "display_order")
    private Integer displayOrder;
}
