package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT wi FROM WarehouseInventory wi WHERE wi.warehouse.id = :warehouseId AND wi.productVariant.id = :variantId")
    Optional<WarehouseInventory> findWithLock(
            @Param("warehouseId") Long warehouseId,
            @Param("variantId") Long variantId
    );

    Optional<WarehouseInventory> findByWarehouseIdAndProductVariantId(Long warehouseId, Long productVariantId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE WarehouseInventory wi " +
           "SET wi.allocatedQty = wi.allocatedQty + :quantity, " +
           "    wi.availableToSellQty = wi.availableToSellQty - :quantity " +
           "WHERE wi.productVariant.id = :variantId " +
           "AND wi.warehouse.id = :warehouseId " +
           "AND wi.availableToSellQty >= :quantity")
    int allocateInventory(
            @Param("variantId") Long variantId,
            @Param("warehouseId") Long warehouseId,
            @Param("quantity") int quantity
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE WarehouseInventory wi " +
           "SET wi.physicalQty = wi.physicalQty - :quantity, " +
           "    wi.allocatedQty = wi.allocatedQty - :quantity " +
           "WHERE wi.productVariant.id = :variantId " +
           "AND wi.warehouse.id = :warehouseId " +
           "AND wi.allocatedQty >= :quantity")
    int confirmShipment(
            @Param("variantId") Long variantId,
            @Param("warehouseId") Long warehouseId,
            @Param("quantity") int quantity
    );

    @Modifying(clearAutomatically = true)
    @Query("UPDATE WarehouseInventory wi " +
           "SET wi.allocatedQty = wi.allocatedQty - :quantity, " +
           "    wi.availableToSellQty = wi.availableToSellQty + :quantity " +
           "WHERE wi.productVariant.id = :variantId " +
           "AND wi.warehouse.id = :warehouseId " +
           "AND wi.allocatedQty >= :quantity")
    int releaseAllocatedInventory(
            @Param("variantId") Long variantId,
            @Param("warehouseId") Long warehouseId,
            @Param("quantity") int quantity
    );

    @Query("SELECT wi FROM WarehouseInventory wi WHERE wi.productVariant.id = :variantId AND wi.allocatedQty >= :quantity")
    java.util.List<WarehouseInventory> findAllocatedInventory(
            @Param("variantId") Long variantId,
            @Param("quantity") int quantity
    );

    java.util.List<WarehouseInventory> findByWarehouseId(Long warehouseId);
}
