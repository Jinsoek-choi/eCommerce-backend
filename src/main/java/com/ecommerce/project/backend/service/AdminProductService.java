package com.ecommerce.project.backend.service;

import com.ecommerce.project.backend.domain.*;
import com.ecommerce.project.backend.dto.ProductDto;
import com.ecommerce.project.backend.dto.ProductImageDto;
import com.ecommerce.project.backend.dto.ProductOptionDto;
import com.ecommerce.project.backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryLinkRepository categoryLinkRepository;

    /** 상품 등록 */
    public Product createProduct(ProductDto productDto) {

        // 1. 상품 유효성 검사
        if (productDto.getStock() < 0) {
            throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        }
        if (productDto.getSellPrice().compareTo(productDto.getConsumerPrice()) > 0) {
            throw new IllegalArgumentException("판매가는 소비자가보다 높을 수 없습니다.");
        }

        // 2. ProductDto를 사용하여 Product 객체 생성
        Product product = new Product(productDto);

        // 3. categoryCode를 받아서 Category 찾기
        String categoryCode = productDto.getCategoryCode();
        Category category = categoryRepository.findByCategoryCode(categoryCode)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카테고리입니다."));

        // 4. 상품 옵션 처리
//        List<ProductOption> options = new ArrayList<>();
        List<ProductOption> options;
        AtomicInteger totalStock = new AtomicInteger(0);

        if (productDto.getIsOption() != null && productDto.getIsOption()) {
            options = productDto.getOptions().stream()
                    .map(optionDto -> {
                        totalStock.addAndGet(optionDto.getStock());  // AtomicInteger로 값을 더함
                        return new ProductOption(optionDto, product);  // DTO -> Entity 변환
                    })
                    .collect(Collectors.toList());
            productOptionRepository.saveAll(options);  // 옵션 일괄 저장
        }

        // 상품의 총 재고 갱신
        product.setStock(totalStock.get());  // AtomicInteger의 값을 상품의 stock에 반영
        Product savedProduct = productRepository.save(product);

        // 5. 대표 이미지 저장 (mainImg 처리)
        if (productDto.getMainImg() != null && !productDto.getMainImg().isEmpty()) {
            ProductImage mainImage = ProductImage.builder()
                    .imageUrl(productDto.getMainImg())  // mainImg URL을 저장
                    .sortOrder(1)  // 대표 이미지는 정렬순서 1로 설정
                    .product(savedProduct)  // 상품과 연결
                    .build();
            productImageRepository.save(mainImage);  // 대표 이미지 저장
        }

        // 6. 상세 이미지 처리 (subImages 처리)
        if (productDto.getSubImages() != null && !productDto.getSubImages().isEmpty()) {
            int sortOrder = 2;  // 상세 이미지의 시작 sortOrder 값 (1은 대표 이미지 사용)

            // 각 imageUrl을 ProductImage 객체로 변환하여 저장
            for (ProductImageDto productImageDto : productDto.getSubImages()) {  // subImages가 String의 리스트
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(productImageDto.getImageUrl())  // imageUrl로 문자열을 전달
                        .sortOrder(sortOrder++)  // 순서대로 증가
                        .product(savedProduct)  // 상품과 연결
                        .build();
                productImageRepository.save(productImage);  // 이미지 저장
            }
        }

        // 8. 카테고리와 상품 연결 (CategoryLink 저장)
        CategoryLink categoryLink = new CategoryLink(savedProduct, category.getCategoryCode());
        categoryLinkRepository.save(categoryLink);  // 카테고리 링크 저장

        return savedProduct;  // 저장된 Product 반환
    }

    /** 상품 수정 */
    public Product updateProduct(Long productId, ProductDto productDto) {

        // 1. 기존 상품 조회
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 2. 상품 유효성 검사 (재고, 가격 등)
        if (productDto.getStock() < 0) {
            throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        }
        if (productDto.getSellPrice().compareTo(productDto.getConsumerPrice()) > 0) {
            throw new IllegalArgumentException("판매가는 소비자가보다 높을 수 없습니다.");
        }

        // 2. 상품 옵션 처리 (상품이 옵션이 있으면 그에 맞는 옵션들을 처리)
        if (productDto.getIsOption() != null && productDto.getIsOption()) {
            List<ProductOption> updatedOptions = productDto.getOptions().stream()
                    .map(ProductOptionDto::fromEntity) // 메서드 참조
                    .collect(Collectors.toList());
            productOptionRepository.saveAll(updatedOptions); // 옵션 일괄 저장
        }

        // 3. 총 재고 처리 (옵션 상품일 경우 옵션 재고 합산, 아니면 상품Dto에서 받은 재고로 업데이트)
        if (existingProduct.getIsOption()) {
            int totalStock = existingProduct.getOptions().stream()
                    .mapToInt(ProductOption::getStock)
                    .sum();
            existingProduct.setStock(totalStock); // 총 재고 갱신
        } else {
            existingProduct.setStock(productDto.getStock()); // 단일 상품일 경우, 직접 받은 재고로 갱신
        }


        // 4. 이미지 처리 (서브 이미지가 있을 경우 처리)
        if (productDto.getSubImages() != null && !productDto.getSubImages().isEmpty()) {
            List<ProductImage> updatedImages = productDto.getSubImages().stream()
                    .map(imageDto -> ProductImage.fromDto(imageDto, productRepository))  // ProductImage로 변환
                    .collect(Collectors.toList());
            productImageRepository.saveAll(updatedImages); // 이미지 저장
        }

        // 5. 카테고리 처리 (상품과 연결된 카테고리 수정)
        List<CategoryLink> categoryLinks = categoryLinkRepository.findByProduct_ProductId(productId);
        CategoryLink categoryLink = categoryLinks.stream()
                .findFirst()  // List에서 첫 번째 항목을 가져옵니다.
                .orElseThrow(() -> new RuntimeException("CategoryLink not found for productId: " + productId));  // 값이 없을 경우 예외 처리
        CategoryLink updatedCategoryLink = new CategoryLink(
                productDto.getCategoryCode(), categoryLink.getProduct());
        categoryLinkRepository.save(updatedCategoryLink);  // 새로운 객체 저장

        // 6. 상품 정보 업데이트 (상품 이름, 가격 등 기본 정보 업데이트)
        existingProduct.updateProductInfo(productDto);

        // 7. 수정된 상품 저장 (모든 수정 사항을 반영하여 상품 저장)
        return productRepository.save(existingProduct); // 수정된 상품 반환

    }
}

//
//    /** 상품 삭제 */
//    public void deleteProduct(Long productId) {
//        if (!productRepository.existsById(productId)) {
//            throw new IllegalArgumentException("삭제할 상품이 존재하지 않습니다. ID: " + productId);
//        }
//        productRepository.deleteById(productId);
//    }


//}