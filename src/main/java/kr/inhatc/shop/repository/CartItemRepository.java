package kr.inhatc.shop.repository;

import kr.inhatc.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 장바구니에 들어갈 상품을 저장하거나 조회하기 위한 레포지토리
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 장바구니 아이디와 상품 아이디로 장바구니에 상품이 들어 있는지 조회
     * @param cartId 장바구니 아이디
     * @param itemId 상품 아이디
     * @return
     */
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
