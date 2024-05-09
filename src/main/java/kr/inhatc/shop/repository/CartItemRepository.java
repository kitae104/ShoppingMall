package kr.inhatc.shop.repository;

import kr.inhatc.shop.dto.CartDetailDto;
import kr.inhatc.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


    /**
     * 장바구니 아이디로 장바구니에 들어 있는 상품 목록을 조회(JPQL)
     * @param cartId 장바구니 아이디
     * @return
     */
    @Query("SELECT new kr.inhatc.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +  // CartDetailDto를 사용하여 조회
            "FROM CartItem ci, ItemImg im " +                                           // CartItem과 ItemImg를 사용하여 조회
            "JOIN ci.item i " +                                                         // ci.item과 i를 조인
            "WHERE ci.cart.id = :cartId " +                                             // cartId로 조회
            "AND im.item.id = ci.item.id " +                                            // im.item.id와 ci.item.id가 같은 것만 조회
            "AND im.repImgYn = 'Y' " +                                                  // 대표 이미지만 조회
            "ORDER BY ci.regTime DESC")                                                 // 등록시간을 기준으로 내림차순 정렬
    List<CartDetailDto> findCartDetailList(@Param("cartId")Long cartId);
}
