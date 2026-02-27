package com.autoflex.challenge.production_management_api.dto.response;

import com.autoflex.challenge.production_management_api.entity.Product;
import com.autoflex.challenge.production_management_api.entity.ProductRawMaterial;

import java.util.ArrayList;
import java.util.List;

public record ProductResponseDTO(
        Long productId,
        String productName,
        String skuCode,
        double productValue,
        List<MaterialSummaryDTO> materialsRequired
) {
    public static ProductResponseDTO fromEntity(Product product) {
        List<MaterialSummaryDTO> materialsRequired = new ArrayList<>();
        for(ProductRawMaterial rel : product.getMaterials()) {
            materialsRequired.add(new MaterialSummaryDTO(rel.getRawMaterial().getName(), rel.getRequiredQuantity()));
        }
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getSkuCode(),
                product.getValue(),
                materialsRequired
        );
    }
}
