package kr.inhatc.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 상품 DTO<br/>
 * 장바구니에 담을 상품의 아읻와 수량을 DTO<br/>
 * 장바구니에 담을 상품의 최소 수량은 1개 이상으로 제안함
 */
@Getter @Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수입니다.")
    private Long itemId;    // 상품 아이디

    @Min(value = 1, message = "상품 수량은 1개 이상이어야 합니다.")
    private int count;      // 상품 수량
}
