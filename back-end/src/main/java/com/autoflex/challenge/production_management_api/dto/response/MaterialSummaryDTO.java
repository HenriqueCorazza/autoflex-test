package com.autoflex.challenge.production_management_api.dto.response;


public record MaterialSummaryDTO(
        String materialName,
        int requiredQuantity
) {
}
