package com.autoflex.challenge.production_management_api.controller;

import com.autoflex.challenge.production_management_api.dto.request.MaterialItemRequest;
import com.autoflex.challenge.production_management_api.dto.request.ProductRequestDTO;
import com.autoflex.challenge.production_management_api.dto.request.ProductSuggestionDTO;
import com.autoflex.challenge.production_management_api.dto.response.MaterialSummaryDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductResponseDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductSuggestionResponse;
import com.autoflex.challenge.production_management_api.exceptions.GlobalExceptionHandler;
import com.autoflex.challenge.production_management_api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponseDTO productResponseDTO;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        productResponseDTO = new ProductResponseDTO(
                1L, "Widget A", "WGT-001", 150.0,
                List.of(new MaterialSummaryDTO("Steel", 10))
        );

        productRequestDTO = new ProductRequestDTO(
                "Widget A", "WGT-001", 150.0,
                List.of(new MaterialItemRequest(1L, 10))
        );
    }

    // ── GET /api/products ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products - should return 200 with list of products")
    void getAllProducts_success() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponseDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Widget A"))
                .andExpect(jsonPath("$[0].productValue").value(150.0));
    }

    // ── GET /api/products/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/{id} - should return 200 with product")
    void getProductById_success() throws Exception {
        when(productService.getProduct(1L)).thenReturn(productResponseDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Widget A"))
                .andExpect(jsonPath("$.skuCode").value("WGT-001"));
    }

    @Test
    @DisplayName("GET /api/products/{id} - should return 404 when not found")
    void getProductById_notFound() throws Exception {
        when(productService.getProduct(99L)).thenThrow(new EntityNotFoundException("Product not found with ID: 99"));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/products/suggestions ───────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/suggestions - should return 200 with suggestions")
    void getProductSuggestions_success() throws Exception {
        ProductSuggestionResponse response = ProductSuggestionResponse.fromData(
                List.of(new ProductSuggestionDTO("Widget A", 10, 1500.0)), 1500.0
        );
        when(productService.getProductSuggestion()).thenReturn(response);

        mockMvc.perform(get("/api/products/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue").value(1500.0))
                .andExpect(jsonPath("$.suggestions[0].productName").value("Widget A"))
                .andExpect(jsonPath("$.suggestions[0].quantityProduced").value(10));
    }

    // ── POST /api/products ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/products - should return 201 with created product")
    void createProduct_success() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(productResponseDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Widget A"));
    }

    @Test
    @DisplayName("POST /api/products - should return 400 when body is invalid")
    void createProduct_invalidBody() throws Exception {
        ProductRequestDTO invalid = new ProductRequestDTO("", "", 0.0, List.of());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/products/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/products/{id} - should return 200 with updated product")
    void updateProduct_success() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class))).thenReturn(productResponseDTO);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Widget A"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} - should return 404 when product not found")
    void updateProduct_notFound() throws Exception {
        when(productService.updateProduct(eq(99L), any(ProductRequestDTO.class)))
                .thenThrow(new EntityNotFoundException("Product not found"));

        mockMvc.perform(put("/api/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequestDTO)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/products/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/products/{id} - should return 204 on success")
    void deleteProduct_success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - should return 404 when not found")
    void deleteProduct_notFound() throws Exception {
        doThrow(new EntityNotFoundException("Product not found")).when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }
}