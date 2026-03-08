package com.shopping.mall.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
	name = "seller",
	indexes = {
		@Index(name = "idx_seller_user_id", columnList = "userId")
	}
)
public class Seller {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 사용자 ID (판매자 계정)
	@Column(nullable = false)
	private Long userId;

	// 브랜드 이름
	@Column(nullable = false, length = 255)
	private String brandName;

	// 사업자 번호
	@Column(nullable = false, length = 50)
	private String businessNumber;

	// 판매자 상태
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SellerStatus status;

	// 생성일
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

}