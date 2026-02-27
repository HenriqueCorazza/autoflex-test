package com.autoflex.challenge.production_management_api.repository;

import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
}
