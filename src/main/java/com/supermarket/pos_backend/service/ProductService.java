package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.dto.ProductDTO;
import com.supermarket.pos_backend.dto.ProductVariantDTO;
import com.supermarket.pos_backend.model.AdminUser;
import com.supermarket.pos_backend.model.Product;
import com.supermarket.pos_backend.model.ProductVariant;
import com.supermarket.pos_backend.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Convert entity to DTO with full URL
    private ProductDTO toDTO(Product product) {
        String imageUrl = product.getImageUrl() != null ? product.getImageUrl() : null;
        String brandName = product.getBrand() != null ? product.getBrand().getName() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        // Map variants
        ProductVariantDTO variantDTOs = new ProductVariantDTO();
        if (product.getVariant() != null) {
            variantDTOs.setWeightValue(product.getVariant().getWeightValue());
            variantDTOs.setPrice(product.getVariant().getPrice());
            variantDTOs.setStockQuantity(product.getVariant().getStockQuantity());
            variantDTOs.setWeightUnit(product.getVariant().getWeightUnit());
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getCategory().getId(),
                product.getBrand().getId(),
                product.getBarcode(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getLowStockThreshold(),
                imageUrl,
                product.isActive(),
                brandName,
                categoryName,
                variantDTOs // pass mapped variants here
        );
    }
    public ProductDTO getProductById(Long id,AdminUser admin) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDTO(product);
    }

    public ProductDTO createProduct(Product product, HttpServletRequest request) {
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    public List<ProductDTO> getAllProductsByAdmin(AdminUser admin) {
        return productRepository.findByAdmin(admin)
                .stream()
                .map(ProductDTO::fromEntity)
                .toList();
    }
    public ProductDTO updateProduct(Long id, Product product, HttpServletRequest request, AdminUser admin) {
        Product existing = productRepository.findByIdAndAdmin(id, admin)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setCategory(product.getCategory());
        existing.setBrand(product.getBrand());
        existing.setPrice(product.getPrice());
        existing.setStockQuantity(product.getStockQuantity());
        existing.setLowStockThreshold(product.getLowStockThreshold());
        existing.setActive(product.isActive());

        if (product.getImageUrl() != null) {
            existing.setImageUrl(product.getImageUrl());
        }

        if (product.getVariant() != null) {
            ProductVariant variant = existing.getVariant();
            if (variant == null) {
                variant = new ProductVariant();
            }
            variant.setWeightValue(product.getVariant().getWeightValue());
            variant.setWeightUnit(product.getVariant().getWeightUnit());
            variant.setPrice(product.getVariant().getPrice());
            variant.setStockQuantity(product.getVariant().getStockQuantity());
            existing.setVariant(variant); // sets both sides
        }

        Product updated = productRepository.save(existing);
        return toDTO(updated);
    }

    private void validateVariants(List<ProductVariant> variants) {
        if (variants == null) return;

        for (ProductVariant v : variants) {
            if (v.getWeightValue() == null || v.getWeightUnit() == null) {
                throw new RuntimeException("Each variant must have weight value and unit");
            }
            if (v.getPrice() == null || v.getPrice().doubleValue() <= 0) {
                throw new RuntimeException("Variant price must be > 0");
            }
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts(AdminUser admin) {
        return productRepository.findByAdminAndStockQuantityLessThanThreshold(admin);
    }

    public Optional<Product> getProductsByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }
}
