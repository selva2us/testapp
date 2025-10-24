package com.supermarket.pos_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermarket.pos_backend.annotations.CurrentAdmin;
import com.supermarket.pos_backend.annotations.CurrentStaff;
import com.supermarket.pos_backend.model.*;
import com.supermarket.pos_backend.dto.ProductDTO;
import com.supermarket.pos_backend.repository.BrandRepository;
import com.supermarket.pos_backend.repository.CategoryRepository;
import com.supermarket.pos_backend.service.CloudinaryService;
import com.supermarket.pos_backend.service.ProductService;

import com.supermarket.pos_backend.service.UploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UploadService uploadService;

    @Autowired
    private CloudinaryService cloudinaryService;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping
    @Operation(summary = "Get all products for logged-in admin or staff")
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @CurrentAdmin (required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff) {

        // Determine whose adminId to use
        Long adminId;
        if (admin != null) {
            adminId = admin.getId(); // Logged-in user is Admin
        } else if (staff != null) {
            adminId = staff.getAdmin().getId(); // Logged-in user is Staff
        } else {
            throw new RuntimeException("Unauthorized user");
        }

        List<ProductDTO> products = productService.getAllProductsByAdminId(adminId);
        return ResponseEntity.ok(products);
    }

    // Get product by ID (only if belongs to this admin)
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID for logged-in admin or staff")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id,
            @CurrentAdmin(required = false) AdminUser admin,
            @CurrentStaff(required = false) StaffUser staff) {

        Long adminId;
        if (admin != null) {
            adminId = admin.getId();
        } else if (staff != null) {
            adminId = staff.getAdmin().getId();
        } else {
            throw new RuntimeException("Unauthorized user");
        }

        ProductDTO product = productService.getProductByIdAndAdminId(id, adminId);
        return ResponseEntity.ok(product);
    }
    // Create product with optional image
    @PostMapping
    @Operation(summary = "Create a new product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@CurrentAdmin AdminUser admin,
            @RequestBody Product product,  // Accept JSON directly
            HttpServletRequest request) {

        // Validate category and brand
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(product.getBrand().getId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        // Set variant if present
        if (product.getVariant() != null) {
            ProductVariant variant = new ProductVariant();
            variant.setWeightValue(product.getVariant().getWeightValue());
            variant.setWeightUnit(product.getVariant().getWeightUnit());
            variant.setPrice(product.getVariant().getPrice());
            variant.setStockQuantity(product.getVariant().getStockQuantity());
            product.setVariant(variant);
        }
        product.setCategory(category);
        product.setBrand(brand);
        product.setAdmin(admin);
        // imageUrl is already set by frontend
        ProductDTO dto = productService.createProduct(product, request);
        return ResponseEntity.ok(dto);
    }

    // Update product with optional image
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@CurrentAdmin AdminUser admin,
            @PathVariable Long id,
            @RequestBody Product product,  // Accept JSON directly
            HttpServletRequest request) {
        ProductDTO dto = productService.updateProduct(id, product, request,admin);
        return ResponseEntity.ok(dto);
    }

    // Delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
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
    public List<Product> getLowStockProducts(@CurrentAdmin AdminUser admin) {
        return productService.getLowStockProducts(admin);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Product> getProductByBarcode(@PathVariable String barcode) {
        return productService.getProductsByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload/excel")
    public String uploadExcel(@CurrentAdmin AdminUser admin,@RequestParam("file") MultipartFile file) {
        return uploadService.importProductsFromExcel(admin,file);
    }

    @PostMapping(path = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file); // upload to Cloudinary
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
