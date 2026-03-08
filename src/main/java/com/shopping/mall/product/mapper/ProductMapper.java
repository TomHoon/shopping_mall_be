package com.shopping.mall.product.mapper;

import com.shopping.mall.product.dto.OptionValueFlatDTO;
import com.shopping.mall.product.dto.ProductDetailResponse;
import com.shopping.mall.product.dto.SkuFlatDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {

	ProductDetailResponse findProduct(Long productId);
	List<OptionValueFlatDTO> findProductOptions(Long productId);
	List<SkuFlatDTO> findProductSkus(Long productId);

}