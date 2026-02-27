package com.autoflex.challenge.production_management_api.dto.request;

public record ProductSuggestionDTO(
        String productName,
        Integer quantityProduced,
        Double subtotal
) {}