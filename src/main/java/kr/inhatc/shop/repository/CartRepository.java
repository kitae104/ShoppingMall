package kr.inhatc.shop.repository;

import kr.inhatc.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 현재 로그인한 회원의 장바구니 조회
     * @param memberId 회원 아이디
     * @return
     */
    Cart findByMemberId(Long memberId);
}
