package com.thaddeus.clothing.repository;

import com.thaddeus.clothing.entity.SizeGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeGuideRepository extends JpaRepository<SizeGuide, Long> {
}
