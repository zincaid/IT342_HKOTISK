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

    private double[] prices;


    private int [] quantity;

    private String[] sizes;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "Product Image is mandatory")
    private String productImage;

    public double getPriceForSize(String size) {
        if (sizes != null && prices != null) {
            for (int i = 0; i < sizes.length; i++) {
                if (sizes[i].equalsIgnoreCase(size)) {
                    return prices[i];
                }
            }
        }
        throw new IllegalArgumentException("Size not found");
    }
}