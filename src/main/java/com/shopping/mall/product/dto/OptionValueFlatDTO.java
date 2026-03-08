package com.shopping.mall.product.dto;

import lombok.Data;

@Data
public class OptionValueFlatDTO {

	private Long optionGroupId;
	private String optionGroupName;

	private Long optionValueId;
	private String optionValue;

}