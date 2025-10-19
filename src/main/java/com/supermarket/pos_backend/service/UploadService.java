package com.supermarket.pos_backend.service;

import com.supermarket.pos_backend.model.*;
import com.supermarket.pos_backend.repository.BrandRepository;
import com.supermarket.pos_backend.repository.CategoryRepository;
import com.supermarket.pos_backend.repository.ProductRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;

@Service
public class UploadService {
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public UploadService(BrandRepository brandRepository, CategoryRepository categoryRepository,
                          ProductRepository productRepository, CloudinaryService cloudinaryService) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public String importProductsFromExcel(MultipartFile file) {
        int count = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header

                Product product = new Product();
                product.setName(row.getCell(0).getStringCellValue());
                String brandName = row.getCell(1).getStringCellValue();
                String categoryName = row.getCell(2).getStringCellValue();

                Brand brand = brandRepository.findByNameIgnoreCase(brandName)
                        .orElseGet(() -> brandRepository.save(new Brand(null, brandName)));
                Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(null, categoryName)));

                product.setBrand(brand);
                product.setCategory(category);
                product.setBarcode(row.getCell(3).getStringCellValue());
                product.setPrice(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                product.setStockQuantity((int) row.getCell(5).getNumericCellValue());
                product.setLowStockThreshold((int) row.getCell(6).getNumericCellValue());
                product.setActive(true);

                // Handle Variant
                ProductVariant variant = new ProductVariant();
                variant.setWeightValue(row.getCell(8).getNumericCellValue());

                String weightUnitStr = row.getCell(9).getStringCellValue().trim().toUpperCase();
                variant.setWeightUnit(WeightUnit.valueOf(weightUnitStr));

                variant.setPrice(BigDecimal.valueOf(row.getCell(10).getNumericCellValue()));
                variant.setStockQuantity((int) row.getCell(11).getNumericCellValue());
                product.setVariant(variant);


                // Upload image from file path or Multipart (optional)
                String imageUrl = row.getCell(7).getStringCellValue();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    String cloudinaryUrl = cloudinaryService.uploadFromUrl(imageUrl);
                    product.setImageUrl(cloudinaryUrl);
                }
/*             String imagePath = row.getCell(7).getStringCellValue();
              if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        String imageUrl = cloudinaryService.uploadFile(imageFile);
                        product.setImageUrl(imageUrl);
                    }
                }*/
                productRepository.save(product);
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return "✅ Successfully imported " + count + " products!";
    }
}
