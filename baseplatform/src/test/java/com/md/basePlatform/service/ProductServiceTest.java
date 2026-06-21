package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.md.basePlatform.domain.Category;
import com.md.basePlatform.domain.Product;
import com.md.basePlatform.domain.ProductDetailResponse;
import com.md.basePlatform.exception.ProductNotFoundException;
import com.md.basePlatform.repository.CategoryRepository;
import com.md.basePlatform.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ProductService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private static final Long PRODUCT_ID = 10L;
    private static final Long CATEGORY_ID = 1L;
    private Product mockProduct;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockProduct = Product.builder()
                .id(PRODUCT_ID).name("iPhone 15").description("最新款智能手机")
                .image("test.jpg").price(new BigDecimal("5999.00"))
                .originalPrice(new BigDecimal("6999.00")).discount(14)
                .sales(5000).rating(4.8).reviewCount(200).stock(100)
                .brand("Apple").spec("256GB").categoryId(CATEGORY_ID)
                .flashSaleEndTime(LocalDateTime.now().plusHours(24))
                .enabled(1).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        mockCategory = new Category();
        mockCategory.setId(CATEGORY_ID);
        mockCategory.setName("电子产品");
        mockCategory.setIcon("el-icon-phone");
        mockCategory.setSortOrder(1);
    }

    // ============ 分页查询商品测试 ============

    @Test
    void should_ReturnProductList_When_ProductsExist() {
        // Given
        Page<Product> productPage = new Page<>(1, 10);
        productPage.setRecords(Collections.singletonList(mockProduct));
        productPage.setTotal(1);

        when(productRepository.selectProducts(any(Page.class), eq(null)))
                .thenReturn(productPage);

        // When
        IPage<ProductDetailResponse> result = productService.getProducts(null, null, null, null, 1, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(1);
        ProductDetailResponse response = result.getRecords().get(0);
        assertThat(response.getId()).isEqualTo(PRODUCT_ID);
        assertThat(response.getName()).isEqualTo("iPhone 15");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("5999.00"));
        assertThat(response.getStock()).isEqualTo(100);
        assertThat(response.getBrand()).isEqualTo("Apple");
    }

    @Test
    void should_ReturnEmptyList_When_NoProductsMatch() {
        // Given
        Page<Product> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        emptyPage.setTotal(0);

        when(productRepository.selectProducts(any(Page.class), any()))
                .thenReturn(emptyPage);

        // When
        IPage<ProductDetailResponse> result = productService.getProducts(CATEGORY_ID, null, null, null, 1, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isZero();
    }

    @Test
    void should_FilterByCategory_When_CategoryIdIsProvided() {
        // Given
        Page<Product> productPage = new Page<>(1, 10);
        productPage.setRecords(Collections.singletonList(mockProduct));
        productPage.setTotal(1);

        when(productRepository.selectProducts(any(Page.class), eq(CATEGORY_ID)))
                .thenReturn(productPage);

        // When
        IPage<ProductDetailResponse> result = productService.getProducts(CATEGORY_ID, null, null, null, 1, 10);

        // Then
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().get(0).getCategoryId()).isEqualTo(CATEGORY_ID);
    }

    @Test
    void should_FilterByPriceRange_When_MinMaxPriceProvided() {
        // Given
        BigDecimal minPrice = new BigDecimal("1000.00");
        BigDecimal maxPrice = new BigDecimal("10000.00");

        Page<Product> productPage = new Page<>(1, 10);
        productPage.setRecords(Collections.singletonList(mockProduct));
        productPage.setTotal(1);

        when(productRepository.selectProducts(any(Page.class), eq(null)))
                .thenReturn(productPage);

        // When
        IPage<ProductDetailResponse> result = productService.getProducts(null, null, minPrice, maxPrice, 1, 10);

        // Then
        assertThat(result.getRecords()).hasSize(1);
    }

    // ============ 获取商品详情测试 ============

    @Test
    void should_GetProductDetailSuccessfully_When_ProductExists() {
        // Given
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(mockProduct);
        when(categoryRepository.selectById(CATEGORY_ID)).thenReturn(mockCategory);

        // When
        ProductDetailResponse response = productService.getProductDetail(PRODUCT_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(PRODUCT_ID);
        assertThat(response.getName()).isEqualTo("iPhone 15");
        assertThat(response.getDescription()).isEqualTo("最新款智能手机");
        assertThat(response.getCategoryId()).isEqualTo(CATEGORY_ID);
        assertThat(response.getCategoryName()).isEqualTo("电子产品");
        assertThat(response.getPromotions()).isNotEmpty();
        assertThat(response.getParams()).containsKey("品牌");
    }

    @Test
    void should_IncludePromotions_When_DiscountExists() {
        // Given
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(mockProduct);
        when(categoryRepository.selectById(CATEGORY_ID)).thenReturn(mockCategory);

        // When
        ProductDetailResponse response = productService.getProductDetail(PRODUCT_ID);

        // Then
        assertThat(response.getPromotions()).hasSizeGreaterThanOrEqualTo(2);
        boolean hasDiscount = response.getPromotions().stream()
                .anyMatch(p -> "sale".equals(p.getType()) && p.getDiscount() != null);
        boolean hasHot = response.getPromotions().stream()
                .anyMatch(p -> "hot".equals(p.getType()));
        assertThat(hasDiscount).isTrue();
        assertThat(hasHot).isTrue();
    }

    @Test
    void should_ThrowProductNotFoundException_When_ProductNotExists() {
        // Given
        when(productRepository.selectById(PRODUCT_ID)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> productService.getProductDetail(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("商品不存在或已下架");
    }

    @Test
    void should_ThrowProductNotFoundException_When_ProductIsDisabled() {
        // Given
        Product disabledProduct = Product.builder()
                .id(PRODUCT_ID).name("iPhone 15").description("最新款智能手机")
                .price(new BigDecimal("5999.00")).enabled(0)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.selectById(PRODUCT_ID)).thenReturn(disabledProduct);

        // When & Then
        assertThatThrownBy(() -> productService.getProductDetail(PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("商品不存在或已下架");
    }

    // ============ 获取分类列表测试 ============

    @Test
    void should_ReturnCategoryList_When_CategoriesExist() {
        // Given
        when(categoryRepository.selectList(null)).thenReturn(Collections.singletonList(mockCategory));

        // When
        List<Category> categories = productService.getCategories();

        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("电子产品");
    }

    @Test
    void should_ReturnEmptyList_When_NoCategoriesExist() {
        // Given
        when(categoryRepository.selectList(null)).thenReturn(Collections.emptyList());

        // When
        List<Category> categories = productService.getCategories();

        // Then
        assertThat(categories).isNotNull();
        assertThat(categories).isEmpty();
    }
}
