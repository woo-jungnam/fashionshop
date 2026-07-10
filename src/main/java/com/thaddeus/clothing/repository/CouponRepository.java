package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.status = 'ACTIVE' AND c.startDate <= CURRENT_TIMESTAMP AND c.endDate >= CURRENT_TIMESTAMP")
    Optional<Coupon> findActiveCoupon(@Param("code") String code);
}
