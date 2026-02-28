package com.autoflex.challenge.production_management_api.service;

import com.autoflex.challenge.production_management_api.dto.request.RawMaterialRequestDTO;
import com.autoflex.challenge.production_management_api.dto.response.RawMaterialResponseDTO;
import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import com.autoflex.challenge.production_management_api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    private RawMaterial rawMaterial;
    private RawMaterialRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        rawMaterial = new RawMaterial();
        rawMaterial.setId(1L);
        rawMaterial.setName("Steel");
        rawMaterial.setSkuCode("STL-001");
        rawMaterial.setStock(50);

        requestDTO = new RawMaterialRequestDTO("Steel", "STL-001", 50);
    }

    // ── getAllRawMaterials ────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllRawMaterials - should return list of DTOs")
    void getAllRawMaterials_success() {
        when(rawMaterialRepository.findAll()).thenReturn(List.of(rawMaterial));

        List<RawMaterialResponseDTO> result = rawMaterialService.getAllRawMaterials();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).materialName()).isEqualTo("Steel");
        assertThat(result.get(0).stock()).isEqualTo(50);
    }

    @Test
    @DisplayName("getAllRawMaterials - should return empty list when none exist")
    void getAllRawMaterials_empty() {
        when(rawMaterialRepository.findAll()).thenReturn(Collections.emptyList());

        List<RawMaterialResponseDTO> result = rawMaterialService.getAllRawMaterials();

        assertThat(result).isEmpty();
    }

    // ── getRawMaterialById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getRawMaterialById - should return DTO when found")
    void getRawMaterialById_success() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));

        RawMaterialResponseDTO result = rawMaterialService.getRawMaterialById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.materialName()).isEqualTo("Steel");
    }

    @Test
    @DisplayName("getRawMaterialById - should throw EntityNotFoundException when not found")
    void getRawMaterialById_notFound() {
        when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.getRawMaterialById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Raw material not found with ID: 99");
    }

    // ── createRawMaterial ────────────────────────────────────────────────────

    @Test
    @DisplayName("createRawMaterial - should create and return DTO")
    void createRawMaterial_success() {
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        RawMaterialResponseDTO result = rawMaterialService.createRawMaterial(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.materialName()).isEqualTo("Steel");
        assertThat(result.skuCode()).isEqualTo("STL-001");
        assertThat(result.stock()).isEqualTo(50);
        verify(rawMaterialRepository, times(1)).save(any(RawMaterial.class));
    }

    // ── updateRawMaterial ────────────────────────────────────────────────────

    @Test
    @DisplayName("updateRawMaterial - should update and return updated DTO")
    void updateRawMaterial_success() {
        RawMaterialRequestDTO updateDTO = new RawMaterialRequestDTO("Steel Updated", "STL-001", 75);
        RawMaterial updated = new RawMaterial();
        updated.setId(1L);
        updated.setName("Steel Updated");
        updated.setSkuCode("STL-001");
        updated.setStock(75);

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(updated);

        RawMaterialResponseDTO result = rawMaterialService.updateRawMaterial(updateDTO, 1L);

        assertThat(result.materialName()).isEqualTo("Steel Updated");
        assertThat(result.stock()).isEqualTo(75);
        verify(rawMaterialRepository, times(1)).save(any(RawMaterial.class));
    }

    @Test
    @DisplayName("updateRawMaterial - should throw EntityNotFoundException when not found")
    void updateRawMaterial_notFound() {
        when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.updateRawMaterial(requestDTO, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Raw material not found");
    }

    // ── deleteRawMaterial ────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteRawMaterial - should delete successfully")
    void deleteRawMaterial_success() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        doNothing().when(rawMaterialRepository).delete(rawMaterial);

        assertThatCode(() -> rawMaterialService.deleteRawMaterial(1L)).doesNotThrowAnyException();
        verify(rawMaterialRepository, times(1)).delete(rawMaterial);
    }

    @Test
    @DisplayName("deleteRawMaterial - should throw EntityNotFoundException when not found")
    void deleteRawMaterial_notFound() {
        when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rawMaterialService.deleteRawMaterial(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Raw material not found");
    }
}