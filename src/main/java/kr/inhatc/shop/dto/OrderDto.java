package kr.inhatc.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 상세 페이지에서 주문할 상품의 정보를 전달하기 위한 DTO
 * 주문은 최소 1, 최대 999로 제한
 */
@Getter
@Setter
public class OrderDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;    // 상품 코드

    @Min(value = 1, message = "최소 1개 이상 주문 가능합니다.")
    @Max(value = 999, message = "최대 999개까지 주문 가능합니다.")
    private int count;      // 주문 수량
}
