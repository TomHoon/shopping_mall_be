package com.shopping.mall.product.dto;

import lombok.Data;

@Data
public class ProductDetailFlatDTO {

	private Long productId;
	private String productName;
	private String description;
	private int basePrice;

	private Long optionGroupId;
	private String optionGroupName;

	private Long optionValueId;
	private String optionValue;

	private Long skuId;
	private String skuCode;
	private int skuPrice;
	private int stock;

}