package com.shopping.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailResponse {
	private Long id;
	private String name;
	private String description;
	private int basePrice;

	private List<OptionGroupDTO> optionGroups;
	private List<SkuDTO> skus;
}
