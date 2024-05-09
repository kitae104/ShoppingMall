package kr.inhatc.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니에 담긴 상품 정보를 담는 DTO 클래스
 */
@Getter @Setter
@AllArgsConstructor
public class CartDetailDto {

    private Long cartItemId;    //장바구니 상품 아이디

    private String itemNm;      //상품명

    private int price;          //상품 금액

    private int count;          //수량

    private String imgUrl;      //상품 이미지 경로

}
