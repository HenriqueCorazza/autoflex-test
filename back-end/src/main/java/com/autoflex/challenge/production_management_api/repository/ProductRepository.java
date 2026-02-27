package com.autoflex.challenge.production_management_api.repository;

import com.autoflex.challenge.production_management_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.materials m LEFT JOIN FETCH m.rawMaterial")
    List<Product> findAllWithMaterials();
}
