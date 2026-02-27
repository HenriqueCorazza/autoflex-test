package com.autoflex.challenge.production_management_api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record MaterialItemRequest(
    @NotNull(message = "Raw material ID is required")
    Long rawMaterialId,

    @NotNull(message = "Quantity is required")
    @Positive(message = "requiredQuantity must be greather than zero")
    int requiredQuantity
    ){}

