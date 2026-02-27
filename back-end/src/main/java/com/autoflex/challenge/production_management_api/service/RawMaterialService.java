package com.autoflex.challenge.production_management_api.service;

import com.autoflex.challenge.production_management_api.dto.request.RawMaterialRequestDTO;
import com.autoflex.challenge.production_management_api.dto.response.RawMaterialResponseDTO;
import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import com.autoflex.challenge.production_management_api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;

    public RawMaterialService(RawMaterialRepository rawMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public List<RawMaterialResponseDTO> getAllRawMaterials() {
        List<RawMaterial> rawMaterials = rawMaterialRepository.findAll();
        return rawMaterials.stream().map(RawMaterialResponseDTO::fromEntity).collect(Collectors.toList());
    }

    public RawMaterialResponseDTO getRawMaterialById(Long id) {
        RawMaterial rawMaterial  = rawMaterialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Raw material not found with ID: "+id));
        return RawMaterialResponseDTO.fromEntity(rawMaterial);
    }

    @Transactional
    public RawMaterialResponseDTO createRawMaterial(RawMaterialRequestDTO rawMaterialDTO) {
        RawMaterial rawMaterial = new RawMaterial();
        dtoToEntity(rawMaterialDTO, rawMaterial);
        return RawMaterialResponseDTO.fromEntity(rawMaterialRepository.save(rawMaterial));

    }

    @Transactional
    public RawMaterialResponseDTO updateRawMaterial(RawMaterialRequestDTO rawMaterialDTO, Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Raw material not found"));
        dtoToEntity(rawMaterialDTO, rawMaterial);
        return RawMaterialResponseDTO.fromEntity(rawMaterialRepository.save(rawMaterial));
    }

    @Transactional
    public void deleteRawMaterial(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Raw material not found"));
        rawMaterialRepository.delete(rawMaterial);
    }

    private void dtoToEntity(RawMaterialRequestDTO rawMaterialDTO, RawMaterial rawMaterial) {
        rawMaterial.setName(rawMaterialDTO.materialName());
        rawMaterial.setStock(rawMaterialDTO.stock());
        rawMaterial.setSkuCode(rawMaterialDTO.skuCode());
    }

}
