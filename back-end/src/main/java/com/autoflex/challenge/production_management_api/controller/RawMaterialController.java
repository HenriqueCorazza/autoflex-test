package com.autoflex.challenge.production_management_api.controller;

import com.autoflex.challenge.production_management_api.dto.request.RawMaterialRequestDTO;
import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import com.autoflex.challenge.production_management_api.service.RawMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
@CrossOrigin(origins = "*")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;
    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRawMaterials() {
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.getAllRawMaterials());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRawMaterialById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.getRawMaterialById(id));
    }

    @PostMapping
    public ResponseEntity<?> createRawMaterial(@Valid @RequestBody RawMaterialRequestDTO rawMaterialRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialService.createRawMaterial(rawMaterialRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRawMaterial(@Valid @RequestBody RawMaterialRequestDTO rawMaterialRequestDTO, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(rawMaterialService.updateRawMaterial(rawMaterialRequestDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRawMaterial(@PathVariable Long id) {
        rawMaterialService.deleteRawMaterial(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
