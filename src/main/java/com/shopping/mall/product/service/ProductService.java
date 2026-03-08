package com.shopping.mall.product.service;

import com.shopping.mall.product.dto.*;
import com.shopping.mall.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductMapper productMapper;

	public ProductDetailResponse getProductDetail(Long productId) {

		ProductDetailResponse product = productMapper.findProduct(productId);

		List<OptionValueFlatDTO> optionFlats =
						productMapper.findProductOptions(productId);

		List<SkuFlatDTO> skuFlats =
						productMapper.findProductSkus(productId);

		// OPTION GROUPING
		Map<Long, OptionGroupDTO> groupMap = new LinkedHashMap<>();

		for (OptionValueFlatDTO flat : optionFlats) {

			OptionGroupDTO group = groupMap.computeIfAbsent(
							flat.getOptionGroupId(),
							id -> {
								OptionGroupDTO g = new OptionGroupDTO();
								g.setId(flat.getOptionGroupId());
								g.setName(flat.getOptionGroupName());
								g.setValues(new ArrayList<>());
								return g;
							}
			);

			OptionValueDTO value = new OptionValueDTO();
			value.setId(flat.getOptionValueId());
			value.setValue(flat.getOptionValue());

			group.getValues().add(value);
		}

		product.setOptionGroups(new ArrayList<>(groupMap.values()));

		// SKU GROUPING
		Map<Long, SkuDTO> skuMap = new LinkedHashMap<>();

		for (SkuFlatDTO flat : skuFlats) {

			SkuDTO sku = skuMap.computeIfAbsent(
							flat.getSkuId(),
							id -> {
								SkuDTO s = new SkuDTO();
								s.setSkuId(flat.getSkuId());
								s.setSkuCode(flat.getSkuCode());
								s.setPrice(flat.getPrice());
								s.setStock(flat.getStock());
								s.setOptions(new ArrayList<>());
								return s;
							}
			);

			SkuOptionDTO option = new SkuOptionDTO();
			option.setOptionGroup(flat.getOptionGroup());
			option.setValue(flat.getOptionValue());

			sku.getOptions().add(option);
		}

		product.setSkus(new ArrayList<>(skuMap.values()));

		return product;
	}
}