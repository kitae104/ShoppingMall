package kr.inhatc.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.inhatc.shop.dto.CartDetailDto;
import kr.inhatc.shop.dto.CartItemDto;
import kr.inhatc.shop.dto.CartOrderDto;
import kr.inhatc.shop.dto.OrderDto;
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
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();                  // 장바구니에 들어 있는 상품 목록을 저장할 리스트

        Member member = memberRepository.findByEmail(email)                         // 현재 로그인 된 회원 이메일로 회원을 찾는다
                .orElseThrow(EntityNotFoundException::new);                         // 회원이 없으면 예외를 발생시킨다

        Cart cart = cartRepository.findByMemberId(member.getId());                  // 회원 ID로 장바구니를 찾는다
        if(cart == null) {                                                          // 장바구니가 없으면 빈 리스트를 반환한다
            return cartDetailDtoList;                                               // 빈 리스트를 반환한다
        }

        cartDetailDtoList = cartItemRepository.findCartDetailList(cart.getId());    // 장바구니에 들어 있는 상품 목록을 조회한다

        return cartDetailDtoList;                                                   // 장바구니에 들어 있는 상품 목록을 반환한다
    }

    /**
     * 로그인 한 회원과 해당 장바구니를 저장한 회원이랑 같은지 검사하는 메소드
     * @param cartItemId 장바구니 상품 ID
     * @param email 회원 이메일
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){

        Member currentMember = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new); // 현재 로그인한 회원을 찾는다
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new); // 장바구니 상품을 찾는다
        Member savedMember = cartItem.getCart().getMember();        // 장바구니 상품에 저장된 회원을 찾는다

        if(!StringUtils.equals(currentMember.getEmail(), savedMember.getEmail())){ // 현재 로그인한 회원과 장바구니 상품에 저장된 회원이 다르면
            return false; // false를 반환한다
        }
        return true;
    }

    /**
     * 장바구니 상품의 수량을 업데이트하는 메소드
     * @param cartItemId 장바구니 상품 ID
     */
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new); // 장바구니 상품을 찾는다
        cartItem.updateCount(count);    // 장바구니 상품의 수량을 변경한다
    }

    /**
     * 장바구니 상품을 삭제하는 메소드
     * @param cartItemId
     */
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new); // 장바구니 상품을 찾는다
        cartItemRepository.delete(cartItem); // 장바구니 상품을 삭제한다
    }

    /**
     * 장바구니에 담긴 상품을 주문하는 메소드<br />
     * 주문 로직으로 전달할 orderDto 리스트를 생성하고 주문을 생성한다<br />
     * 주문한 상품은 장바구니에서 제거한다<br />
     * @param cartOrderDtoList 장바구니 상품 목록
     * @param email 회원 이메일
     * @return 주문번호
     */
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        List<OrderDto> orderDtoList = new ArrayList<>();            // 주문 정보 리스트 생성

        // 장바구니 상품 목록을 순회하면서 주문 정보를 생성
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {         // 장바구니 상품 목록을 순회
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);    // 장바구니 상품 조회
            OrderDto orderDto = new OrderDto();                     // 주문 정보 생성
            orderDto.setItemId(cartItem.getItem().getId());         // 상품 ID 추가
            orderDto.setCount(cartItem.getCount());                 // 수량 추가
            orderDtoList.add(orderDto);                             // 주문 정보 추가
        }

        Long orderId = orderService.orders(orderDtoList, email);    // 주문 생성 -> 주문번호 반환

        // 주문한 상품에 대해서 장바구니에서 제거
        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);    // 장바구니 상품 조회
            cartItemRepository.delete(cartItem);                   // 장바구니 상품 삭제
        }
        return orderId;    // 주문번호 반환
    }
}
