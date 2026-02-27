package com.autoflex.challenge.production_management_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RawMaterialRequestDTO(
        @NotBlank
        String materialName,
        @NotBlank(message = "SKU code is required")
        String skuCode,
        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Must be a positive number")
        int stock
) {}
