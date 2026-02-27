package com.autoflex.challenge.production_management_api.dto.response;

import com.autoflex.challenge.production_management_api.dto.request.ProductSuggestionDTO;

import java.util.List;

public record ProductSuggestionResponse(
        List<ProductSuggestionDTO> suggestions,
        Double totalValue
) {
    public static ProductSuggestionResponse fromData(List<ProductSuggestionDTO> suggestions, double totalValue) {
        return new ProductSuggestionResponse(
                suggestions,
                totalValue
        );
    }
}
