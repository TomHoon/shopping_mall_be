package com.shopping.mall.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class OptionGroupDTO {

	private Long id;
	private String name;
	private List<OptionValueDTO> values;

}
