package kr.inhatc.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.inhatc.shop.dto.CartItemDto;
import kr.inhatc.shop.entity.Cart;
import kr.inhatc.shop.entity.CartItem;
import kr.inhatc.shop.entity.Item;
import kr.inhatc.shop.entity.Member;
import kr.inhatc.shop.repository.CartItemRepository;
import kr.inhatc.shop.repository.CartRepository;
import kr.inhatc.shop.repository.ItemRepository;
import kr.inhatc.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니에 상품을 담는 로직을 작성하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    /**
     * 장바구니에 상품을 담는 메소드
     * @param cartItemDto 장바구니에 담을 상품 정보
     * @param email 회원 이메일
     * @return 장바구니에 담긴 상품의 ID
     */
    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId())    // 상품 ID로 상품을 찾는다
                .orElseThrow(EntityNotFoundException::new);             // 상품이 없으면 예외를 발생시킨다
        Member member = memberRepository.findByEmail(email)             // 회원 이메일로 회원을 찾는다
                .orElseThrow(EntityNotFoundException::new);             // 회원이 없으면 예외를 발생시킨다

        Cart cart = cartRepository.findByMemberId(member.getId());      // 회원 ID로 장바구니를 찾는다

        if(cart == null){                               // 장바구니가 없으면 새로 생성한다
            cart = Cart.createCart(member);             // 장바구니를 생성한다
            cartRepository.save(cart);                  // 장바구니를 저장한다
        }

        CartItem savedCartItem = cartItemRepository                     // 현재 상품이 장바구니에 이미 들어 있는지 확인
                .findByCartIdAndItemId(cart.getId(), item.getId());     // 장바구니 ID와 상품 ID로 찾는다

        if(savedCartItem != null){                              // 이미 장바구니에 들어 있는 상품이면 수량만 추가한다
            savedCartItem.addCount(cartItemDto.getCount());     // 수량을 추가한다
            return savedCartItem.getId();                       // 상품 ID를 반환한다
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());    // 장바구니에 상품을 추가한다
            cartItemRepository.save(cartItem);      // 장바구니에 들어갈 상품을 저장한다
            return cartItem.getId();                // 상품 ID를 반환한다
        }
    }
}
