package com.shopping.mall.product.controller;

import com.shopping.mall.common.response.ApiResponse;
import com.shopping.mall.product.dto.ProductDetailResponse;
import com.shopping.mall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	@GetMapping("/{productId}")
	public ApiResponse<ProductDetailResponse> getProduct(
					@PathVariable Long productId) {

		ProductDetailResponse response =
						productService.getProductDetail(productId);

		return ApiResponse.success(response);
	}
}