package com.shopping.mall.product.entity;

import com.shopping.mall.common.entity.BaseEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
	name = "sku_option",
	indexes = {
		@Index(name = "idx_sku_option_sku_id", columnList = "skuId")
	}
)
public class SkuOption extends BaseEntity {

	@EmbeddedId
	private SkuOptionId id;

}