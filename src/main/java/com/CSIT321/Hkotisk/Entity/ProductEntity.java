package com.CSIT321.Hkotisk.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity implements Serializable {
    private static final long serialVersionUID = -7446162716367847201L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    @Positive(message = "Price must be positive")
    private double price;

    @PositiveOrZero(message = "Quantity must be zero or positive")
    private int quantity;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "Product Image is mandatory")
    private String productImage;

    @PrePersist
    @PreUpdate
    private void updateAvailability() {
        if (quantity < 0) quantity = 0; // Prevent negative quantities
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public double getPrice() {
        return price;
    }
}
