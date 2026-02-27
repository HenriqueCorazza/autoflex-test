package com.autoflex.challenge.production_management_api.dto.response;

import com.autoflex.challenge.production_management_api.entity.RawMaterial;

public record RawMaterialResponseDTO(
        Long id,
        String materialName,
        String skuCode,
        int stock
) {
    public static RawMaterialResponseDTO fromEntity(RawMaterial rawMaterial) {
        return new RawMaterialResponseDTO(
                rawMaterial.getId(),
                rawMaterial.getName(),
                rawMaterial.getSkuCode(),
                rawMaterial.getStock()
        );
    }
}
