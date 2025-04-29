package com.CSIT321.Hkotisk.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class cartDTO {

    @NotNull(message = "Product ID is mandatory")
    @Positive(message = "Product ID must be positive")
    private int productId;

    @NotNull(message = "Quantity is mandatory")
    @Positive(message = "Quantity must be positive")
    private int quantity;

    // Product price
    @Positive(message = "Price must be positive")
    private double price = 0.0;

}
