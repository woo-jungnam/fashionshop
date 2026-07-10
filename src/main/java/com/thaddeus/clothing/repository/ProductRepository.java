package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN p.variants v " +
           "JOIN v.attributeValues av " +
           "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
           "AND (:minPrice IS NULL OR COALESCE(v.salePrice, v.price) >= :minPrice) " +
           "AND (:maxPrice IS NULL OR COALESCE(v.salePrice, v.price) <= :maxPrice) " +
           "AND (COALESCE(:sizes) IS NULL OR av.attribute.name = 'Size' AND av.value IN :sizes) " +
           "AND (COALESCE(:colors) IS NULL OR av.attribute.name = 'Color' AND av.value IN :colors) " +
           "AND p.deleted = false")
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("sizes") List<String> sizes,
            @Param("colors") List<String> colors,
            Pageable pageable
    );
}
