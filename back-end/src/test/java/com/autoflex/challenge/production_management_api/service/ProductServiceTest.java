package com.autoflex.challenge.production_management_api.service;

import com.autoflex.challenge.production_management_api.dto.request.MaterialItemRequest;
import com.autoflex.challenge.production_management_api.dto.request.ProductRequestDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductResponseDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductSuggestionResponse;
import com.autoflex.challenge.production_management_api.entity.Product;
import com.autoflex.challenge.production_management_api.entity.ProductRawMaterial;
import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import com.autoflex.challenge.production_management_api.repository.ProductRepository;
import com.autoflex.challenge.production_management_api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductService productService;

    private RawMaterial rawMaterial;
    private Product product;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setUp() {
        rawMaterial = new RawMaterial();
        rawMaterial.setId(1L);
        rawMaterial.setName("Steel");
        rawMaterial.setSkuCode("STL-001");
        rawMaterial.setStock(100);

        product = new Product();
        product.setId(1L);
        product.setName("Widget A");
        product.setSkuCode("WGT-001");
        product.setValue(150.0);
        product.setMaterials(new ArrayList<>());

        ProductRawMaterial prm = new ProductRawMaterial();
        prm.setId(1L);
        prm.setProduct(product);
        prm.setRawMaterial(rawMaterial);
        prm.setRequiredQuantity(10);
        product.getMaterials().add(prm);

        productRequestDTO = new ProductRequestDTO(
                "Widget A",
                "WGT-001",
                150.0,
                List.of(new MaterialItemRequest(1L, 10))
        );
    }

    // ── createProduct ────────────────────────────────────────────────────────

    @Test
    @DisplayName("createProduct - should create and return product DTO")
    void createProduct_success() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.productName()).isEqualTo("Widget A");
        assertThat(result.productValue()).isEqualTo(150.0);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct - should throw EntityNotFoundException when material not found")
    void createProduct_materialNotFound() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(productRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Material not found with ID: 1");
    }

    // ── updateProduct ────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProduct - should update and return updated product DTO")
    void updateProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.saveAndFlush(any(Product.class))).thenReturn(product);
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.updateProduct(1L, productRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.productName()).isEqualTo("Widget A");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct - should throw EntityNotFoundException when product not found")
    void updateProduct_productNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, productRequestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    // ── deleteProduct ────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteProduct - should delete product successfully")
    void deleteProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        assertThatCode(() -> productService.deleteProduct(1L)).doesNotThrowAnyException();
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("deleteProduct - should throw EntityNotFoundException when product not found")
    void deleteProduct_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    // ── getProduct ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProduct - should return product DTO by ID")
    void getProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO result = productService.getProduct(1L);

        assertThat(result).isNotNull();
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productName()).isEqualTo("Widget A");
    }

    @Test
    @DisplayName("getProduct - should throw EntityNotFoundException when not found")
    void getProduct_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with ID: 99");
    }

    // ── getAllProducts ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllProducts - should return list of product DTOs")
    void getAllProducts_success() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productName()).isEqualTo("Widget A");
    }

    @Test
    @DisplayName("getAllProducts - should return empty list when no products")
    void getAllProducts_empty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    // ── getProductSuggestion ─────────────────────────────────────────────────

    @Test
    @DisplayName("getProductSuggestion - should return suggestion with correct quantity and total value")
    void getProductSuggestion_success() {
        // Stock: 100 units of steel, product needs 10 per unit → can produce 10
        when(productRepository.findAllWithMaterials()).thenReturn(List.of(product));
        when(rawMaterialRepository.findAll()).thenReturn(List.of(rawMaterial));

        ProductSuggestionResponse response = productService.getProductSuggestion();

        assertThat(response.suggestions()).hasSize(1);
        assertThat(response.suggestions().get(0).productName()).isEqualTo("Widget A");
        assertThat(response.suggestions().get(0).quantityProduced()).isEqualTo(10);
        assertThat(response.totalValue()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("getProductSuggestion - should throw EntityNotFoundException when no products")
    void getProductSuggestion_noProducts() {
        when(productRepository.findAllWithMaterials()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> productService.getProductSuggestion())
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No products found");
    }

    @Test
    @DisplayName("getProductSuggestion - should return empty suggestions when stock is zero")
    void getProductSuggestion_insufficientStock() {
        rawMaterial.setStock(0);
        when(productRepository.findAllWithMaterials()).thenReturn(List.of(product));
        when(rawMaterialRepository.findAll()).thenReturn(List.of(rawMaterial));

        ProductSuggestionResponse response = productService.getProductSuggestion();

        assertThat(response.suggestions()).isEmpty();
        assertThat(response.totalValue()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("getProductSuggestion - should prioritize product with highest value")
    void getProductSuggestion_prioritizeByValue() {
        // Product B is cheaper
        Product productB = new Product();
        productB.setId(2L);
        productB.setName("Widget B");
        productB.setSkuCode("WGT-002");
        productB.setValue(50.0);
        productB.setMaterials(new ArrayList<>());

        ProductRawMaterial prmB = new ProductRawMaterial();
        prmB.setId(2L);
        prmB.setProduct(productB);
        prmB.setRawMaterial(rawMaterial);
        prmB.setRequiredQuantity(10);
        productB.getMaterials().add(prmB);

        // Stock = 15, product A needs 10, product B needs 10
        // Should produce 1 of A (costs 10 stock), then 0 of B (only 5 left)
        rawMaterial.setStock(15);

        when(productRepository.findAllWithMaterials()).thenReturn(List.of(product, productB));
        when(rawMaterialRepository.findAll()).thenReturn(List.of(rawMaterial));

        ProductSuggestionResponse response = productService.getProductSuggestion();

        assertThat(response.suggestions()).hasSize(1);
        assertThat(response.suggestions().get(0).productName()).isEqualTo("Widget A");
        assertThat(response.totalValue()).isEqualTo(150.0);
    }
}