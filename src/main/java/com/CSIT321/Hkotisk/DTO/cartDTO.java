package com.CSIT321.Hkotisk.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class cartDTO {

    @NotBlank(message = "Product ID is mandatory")
    private int productId;

    private String size;

    @NotNull(message = "Quantity is mandatory")
    private int quantity;

    // The price changes for products with different sizes
    @Positive(message = "Price must be positive")
    private double price = 0.0;

}