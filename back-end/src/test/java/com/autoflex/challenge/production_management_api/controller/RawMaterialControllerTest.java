package com.autoflex.challenge.production_management_api.controller;

import com.autoflex.challenge.production_management_api.dto.request.RawMaterialRequestDTO;
import com.autoflex.challenge.production_management_api.dto.response.RawMaterialResponseDTO;
import com.autoflex.challenge.production_management_api.exceptions.GlobalExceptionHandler;
import com.autoflex.challenge.production_management_api.service.RawMaterialService;
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
class RawMaterialControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private RawMaterialService rawMaterialService;

    @InjectMocks
    private RawMaterialController rawMaterialController;

    private RawMaterialResponseDTO responseDTO;
    private RawMaterialRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rawMaterialController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        responseDTO = new RawMaterialResponseDTO(1L, "Steel", "STL-001", 50);
        requestDTO = new RawMaterialRequestDTO("Steel", "STL-001", 50);
    }

    // ── GET /api/raw-materials ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/raw-materials - should return 200 with list")
    void getAllRawMaterials_success() throws Exception {
        when(rawMaterialService.getAllRawMaterials()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/raw-materials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materialName").value("Steel"))
                .andExpect(jsonPath("$[0].stock").value(50));
    }

    // ── GET /api/raw-materials/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("GET /api/raw-materials/{id} - should return 200 with material")
    void getRawMaterialById_success() throws Exception {
        when(rawMaterialService.getRawMaterialById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/raw-materials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materialName").value("Steel"))
                .andExpect(jsonPath("$.skuCode").value("STL-001"));
    }

    @Test
    @DisplayName("GET /api/raw-materials/{id} - should return 404 when not found")
    void getRawMaterialById_notFound() throws Exception {
        when(rawMaterialService.getRawMaterialById(99L))
                .thenThrow(new EntityNotFoundException("Raw material not found with ID: 99"));

        mockMvc.perform(get("/api/raw-materials/99"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/raw-materials ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/raw-materials - should return 201 with created material")
    void createRawMaterial_success() throws Exception {
        when(rawMaterialService.createRawMaterial(any(RawMaterialRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.materialName").value("Steel"));
    }

    @Test
    @DisplayName("POST /api/raw-materials - should return 400 when body is invalid")
    void createRawMaterial_invalidBody() throws Exception {
        RawMaterialRequestDTO invalid = new RawMaterialRequestDTO("", "", -1);

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/raw-materials/{id} ──────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/raw-materials/{id} - should return 200 with updated material")
    void updateRawMaterial_success() throws Exception {
        when(rawMaterialService.updateRawMaterial(any(RawMaterialRequestDTO.class), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/raw-materials/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materialName").value("Steel"));
    }

    @Test
    @DisplayName("PUT /api/raw-materials/{id} - should return 404 when not found")
    void updateRawMaterial_notFound() throws Exception {
        when(rawMaterialService.updateRawMaterial(any(RawMaterialRequestDTO.class), eq(99L)))
                .thenThrow(new EntityNotFoundException("Raw material not found"));

        mockMvc.perform(put("/api/raw-materials/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/raw-materials/{id} ───────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/raw-materials/{id} - should return 204 on success")
    void deleteRawMaterial_success() throws Exception {
        doNothing().when(rawMaterialService).deleteRawMaterial(1L);

        mockMvc.perform(delete("/api/raw-materials/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/raw-materials/{id} - should return 404 when not found")
    void deleteRawMaterial_notFound() throws Exception {
        doThrow(new EntityNotFoundException("Raw material not found")).when(rawMaterialService).deleteRawMaterial(99L);

        mockMvc.perform(delete("/api/raw-materials/99"))
                .andExpect(status().isNotFound());
    }
}