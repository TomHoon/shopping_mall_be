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
	name = "sku",
	indexes = {
		@Index(name = "idx_sku_product_id", columnList = "product_id"),
		@Index(name = "idx_sku_status", columnList = "status")
	}
)
public class Sku extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "sku_code", nullable = false, unique = true)
	private String skuCode;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int stock;

	@Column(nullable = false)
	private String status;

}