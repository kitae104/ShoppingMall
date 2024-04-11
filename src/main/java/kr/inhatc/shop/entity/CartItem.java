package kr.inhatc.shop.entity;

import jakarta.persistence.*;
import kr.inhatc.shop.utils.audit.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Long id;                        // 장바구니 상품 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;                      // 장바구니

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;                      // 상품

    private int count;                      // 상품 수량

    /**
     * 장바구니에 담을 상품 생성
     * @param cart 장바구니
     * @param item 상품
     * @param count 수량
     * @return 장바구니 상품
     */
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();  // 장바구니 상품 생성
        cartItem.setCart(cart);              // 장바구니 설정
        cartItem.setItem(item);              // 상품 설정
        cartItem.setCount(count);            // 수량 설정
        return cartItem;                     // 장바구니 상품 반환
    }

    /**
     * 장바구니에 담을 수량 추가<br/>
     * 기존 상품에 수량을 추가할 때 사용
     * @param count 추가할 수량
     */
    public void addCount(int count) {
        this.count += count;                 // 수량 추가
    }
}
