package kr.inhatc.shop.entity;

import jakarta.persistence.*;
import kr.inhatc.shop.utils.audit.BaseEntity;
import lombok.*;

/**
 * 주문 상품 엔티티
 */

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;                        // 주문 상품 코드

    @ManyToOne(fetch = FetchType.LAZY)      // 다대일 관계이므로 연관관계의 주인은 다(N)쪽에 설정한다.
    @JoinColumn(name = "order_id")          // FK를 지정한다.
    private Order order;                    // 주문

    @ManyToOne(fetch = FetchType.LAZY)      // 다대일 관계이므로 연관관계의 주인은 다(N)쪽에 설정한다.
    @JoinColumn(name = "item_id")           // FK를 지정한다.
    private Item item;                      // 주문 상품

    private int orderPrice;                 // 주문 가격

    private int count;                      // 주문 수량

    /**
     * 주문 상품 엔티티 생성 메서드
     */
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();      // 주문 상품 엔티티 생성
        orderItem.setItem(item);                    // 주문 상품 설정
        orderItem.setOrderPrice(item.getPrice());   // 주문 가격 설정
        orderItem.setCount(count);                  // 주문 수량 설정
        item.removeStock(count);                    // 주문 수량만큼 재고 감소
        return orderItem;                           // 주문 상품 엔티티 반환
    }

    /**
     * 주문 상품 가격 조회 메서드
     */
    public int getTotalPrice() {
        return orderPrice * count;          // 주문 가격 * 주문 수량
    }
}
