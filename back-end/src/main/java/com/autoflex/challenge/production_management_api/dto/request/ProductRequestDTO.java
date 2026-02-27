package com.autoflex.challenge.production_management_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required")
    String productName,
    String skuCode,
    @NotNull(message = "Product value cannot be null")
    @Positive(message = "Product value must be greater than zero")
    double productValue,
    @NotEmpty(message = "At least one raw material is required")
    @Valid
    List<MaterialItemRequest> materialsRequired
){}
