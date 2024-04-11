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
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 회원당 1개의 장바구니만 생성 가능<br/>
     * 처음에 장바구니에 상품을 담을 때 해당 회원의 장바구니를 생성
     * @param member
     * @return
     */
    public static Cart createCart(Member member) {
        Cart cart = new Cart();         // 장바구니 생성
        cart.setMember(member);         // 회원 설정
        return cart;                    // 장바구니 반환
    }
}
