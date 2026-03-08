package com.shopping.mall.product.dto;

import lombok.Data;

@Data
public class SkuFlatDTO {

	private Long skuId;
	private String skuCode;
	private int price;
	private int stock;

	private String optionGroup;
	private String optionValue;

}