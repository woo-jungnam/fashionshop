package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseInventory, Long> {
    List<WarehouseInventory> findByProductVariantId(Long productVariantId);
}
