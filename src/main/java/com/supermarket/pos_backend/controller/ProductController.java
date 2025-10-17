package com.supermarket.pos_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.pos_backend.model.Brand;
import com.supermarket.pos_backend.model.Category;
import com.supermarket.pos_backend.model.Product;
import com.supermarket.pos_backend.dto.ProductDTO;
import com.supermarket.pos_backend.repository.BrandRepository;
import com.supermarket.pos_backend.repository.CategoryRepository;
import com.supermarket.pos_backend.service.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // Get all products
    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductDTO>> getAllProducts(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProducts(request));
    }

    // Get product by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(productService.getProductById(id, request));
    }

    // Create product with optional image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) throws IOException {

        // Deserialize JSON manually
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        // Your existing logic
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(product.getBrand().getId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image);
            product.setImageUrl(fileName);
        }

        product.setCategory(category);
        product.setBrand(brand);

        ProductDTO dto = productService.createProduct(product, request);
        return ResponseEntity.ok(dto);
    }

    // Update product with optional image
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestPart("product") Product product,
                                                    @RequestPart(value = "image", required = false) MultipartFile image,
                                                    HttpServletRequest request) throws IOException {

        if (image != null && !image.isEmpty()) {
            String fileName = saveImage(image);
            product.setImageUrl(fileName);
        }

        ProductDTO dto = productService.updateProduct(id, product, request);
        return ResponseEntity.ok(dto);
    }

    // Delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Utility method to save uploaded image
    private String saveImage(MultipartFile file) throws IOException {
        // Ensure the uploads folder exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file with original filename
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return UPLOAD_DIR + fileName; // stored relative path
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts() {
        return productService.getLowStockProducts();
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Product> getProductByBarcode(@PathVariable String barcode) {
        return productService.getProductsByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
