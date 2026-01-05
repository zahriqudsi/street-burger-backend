package com.streetburger.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_si", length = 100)
    private String nameSi; // Sinhala

    @Column(name = "name_ta", length = 100)
    private String nameTa; // Tamil

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
