package com.shopping.mall.guide.repository;

import com.shopping.mall.guide.entity.GuideUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideUserRepository extends JpaRepository<GuideUser, Long> {
}