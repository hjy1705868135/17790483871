package com.md.basePlatform.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.md.basePlatform.domain.Product;
import com.md.basePlatform.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductMapper测试类
 */
@SpringBootTest
public class ProductMapperTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSelectProducts() {
        // 测试分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        
        IPage<Product> result = productRepository.selectProducts(page, null);
        
        assertNotNull(result);
        System.out.println("Total records: " + result.getTotal());
        System.out.println("Current page records: " + result.getRecords().size());
    }

    @Test
    void testSelectProductsWithFilters() {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        
        // 测试分类筛选
        IPage<Product> result = productRepository.selectProducts(page, 1L);
        
        assertNotNull(result);
        System.out.println("Filtered records: " + result.getTotal());
    }

    @Test
    void testSelectProductsWithCategory() {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Product> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        
        // 测试分类筛选（假设分类ID为1存在）
        IPage<Product> result = productRepository.selectProducts(page, 1L);
        
        assertNotNull(result);
        System.out.println("Category 1 records: " + result.getTotal());
    }
}