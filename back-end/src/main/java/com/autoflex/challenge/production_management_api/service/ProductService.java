package com.autoflex.challenge.production_management_api.service;


import com.autoflex.challenge.production_management_api.dto.request.MaterialItemRequest;
import com.autoflex.challenge.production_management_api.dto.request.ProductRequestDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductResponseDTO;
import com.autoflex.challenge.production_management_api.dto.request.ProductSuggestionDTO;
import com.autoflex.challenge.production_management_api.dto.response.ProductSuggestionResponse;
import com.autoflex.challenge.production_management_api.entity.Product;
import com.autoflex.challenge.production_management_api.entity.ProductRawMaterial;
import com.autoflex.challenge.production_management_api.entity.RawMaterial;
import com.autoflex.challenge.production_management_api.repository.ProductRepository;
import com.autoflex.challenge.production_management_api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;


    public ProductService(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }


    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = new Product();
        dtoToEntity(dto, product);

        return ProductResponseDTO.fromEntity(productRepository.save(product));
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto)  {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.getMaterials().clear();
        productRepository.saveAndFlush(product);
        dtoToEntity(dto, product);

        return ProductResponseDTO.fromEntity(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    public ProductResponseDTO getProduct(Long id){
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + id));
        return ProductResponseDTO.fromEntity(product);
    }

    public List<ProductResponseDTO> getAllProducts(){
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new EntityNotFoundException("No products found");
        }
        return products.stream().map(ProductResponseDTO::fromEntity).collect(Collectors.toList());
    }

    public ProductSuggestionResponse getProductSuggestion(){
        double totalValue = 0;

        List<Product> products = productRepository.findAllWithMaterials().stream().sorted(Comparator.comparing(Product::getValue)
                .reversed()).collect(Collectors.toList());
        if (products.isEmpty()) {
            throw new EntityNotFoundException("No products found");
        }

        Map<Long, Integer> inventory = rawMaterialRepository.findAll().stream().collect(Collectors.toMap(RawMaterial::getId, RawMaterial::getStock));
        if (inventory == null) {
            throw new EntityNotFoundException("No raw materials found");
        }

        List<ProductSuggestionDTO> results = new ArrayList<>();

        for (Product p : products) {
            int quantityProduced = 0;
            while (hasStock(p, inventory)){
                consumeStock(p, inventory);
                quantityProduced++;
            }

            if(quantityProduced > 0){
                double subtotal = quantityProduced * p.getValue();
                results.add(new ProductSuggestionDTO(p.getName(), quantityProduced, subtotal));
                totalValue += subtotal;
            }
        }
        return ProductSuggestionResponse.fromData(results, totalValue);
    }

    private void consumeStock(Product p, Map<Long, Integer> inventory){

        for(ProductRawMaterial pm : p.getMaterials()){
            int required = pm.getRequiredQuantity();
            int remaining = inventory.get(pm.getRawMaterial().getId());
            inventory.put(pm.getRawMaterial().getId(), remaining - required);
        }
    }

    private boolean hasStock(Product p, Map<Long, Integer> inventory) {
        if(p.getMaterials().isEmpty())
            return false;
        for(ProductRawMaterial pm : p.getMaterials()) {
            int required = pm.getRequiredQuantity();

            Long materialId = pm.getRawMaterial().getId();
            int amount = inventory.get(materialId);

            if(amount < required)
                return false;
        }
        return true;
    }
    private void dtoToEntity(ProductRequestDTO dto, Product product) {
        product.setName(dto.productName());
        product.setSkuCode(dto.skuCode());
        product.setValue(dto.productValue());

        for (MaterialItemRequest materialDTO : dto.materialsRequired()) {
            RawMaterial rawMaterial = rawMaterialRepository.findById(materialDTO.rawMaterialId())
                    .orElseThrow(() -> new EntityNotFoundException("Material not found with ID: " + materialDTO.rawMaterialId()));

            ProductRawMaterial relational = new ProductRawMaterial();
            relational.setRawMaterial(rawMaterial);
            relational.setProduct(product);
            relational.setRequiredQuantity(materialDTO.requiredQuantity());

            product.getMaterials().add(relational);
        }
    }
}
