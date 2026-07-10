package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.ChannelProductMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelProductMappingRepository extends JpaRepository<ChannelProductMapping, Long> {
    List<ChannelProductMapping> findBySalesChannelId(Long salesChannelId);
    Optional<ChannelProductMapping> findBySalesChannelIdAndProductVariantId(Long salesChannelId, Long productVariantId);
}
