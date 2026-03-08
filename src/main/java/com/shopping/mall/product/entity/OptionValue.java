package com.shopping.mall.product.entity;

import com.shopping.mall.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
	name = "option_value",
	indexes = {
		@Index(name = "idx_option_value_option_group_id", columnList = "option_group_id")
	}
)
public class OptionValue extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "option_group_id", nullable = false)
	private Long optionGroupId;

	@Column(nullable = false)
	private String value;

}