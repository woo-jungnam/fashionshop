package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    @EntityGraph(attributePaths = {"returnItems", "returnItems.orderItem", "refundTransaction"})
    Optional<ReturnRequest> findById(Long id);

    @EntityGraph(attributePaths = {"returnItems", "returnItems.orderItem"})
    Page<ReturnRequest> findByOrderId(Long orderId, Pageable pageable);

    @EntityGraph(attributePaths = {"returnItems", "returnItems.orderItem"})
    Page<ReturnRequest> findByOrderUserId(Long userId, Pageable pageable);
}
