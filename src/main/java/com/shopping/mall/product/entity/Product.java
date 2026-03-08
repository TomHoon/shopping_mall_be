package com.shopping.mall.product.entity;

import com.shopping.mall.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
	name = "product",
	indexes = {
		@Index(name = "idx_product_seller_id", columnList = "seller_id"),
		@Index(name = "idx_product_status", columnList = "status")
  }
)
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "seller_id", nullable = false)
	private Long sellerId;

	@Column(nullable = false)
	private String name;

	private String description;

	@Column(name = "base_price", nullable = false)
	private int basePrice;

	@Column(nullable = false)
	private String status;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

}
