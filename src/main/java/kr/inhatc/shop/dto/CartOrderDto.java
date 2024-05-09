package kr.inhatc.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 장바구니에 담을 상품 정보를 담는 DTO 클래스
 */
@Getter @Setter
public class CartOrderDto {
    private Long cartItemId;                        // 장바구니 상품 ID
    private List<CartOrderDto> cartOrderDtoList;    // 장바구니 상품 목록
}
