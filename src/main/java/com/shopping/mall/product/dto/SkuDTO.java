package com.shopping.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkuDTO {

	private Long skuId;
	private String skuCode;
	private int price;
	private int stock;

	private List<SkuOptionDTO> options;

}
