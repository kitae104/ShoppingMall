package kr.inhatc.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.inhatc.shop.dto.OrderDto;
import kr.inhatc.shop.dto.OrderHistDto;
import kr.inhatc.shop.dto.OrderItemDto;
import kr.inhatc.shop.entity.*;
import kr.inhatc.shop.repository.ItemImgRepository;
import kr.inhatc.shop.repository.ItemRepository;
import kr.inhatc.shop.repository.MemberRepository;
import kr.inhatc.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 로직 작성 서비스
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    /**
     * 주문 로직
     * @param orderDto 주문 정보
     * @param email 회원 이메일
     * @return 주문번호
     */
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);    // 상품 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);    // 회원 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());    // 주문상품 생성
        orderItemList.add(orderItem);   // 주문상품 리스트에 추가

        Order order = Order.createOrder(member, orderItemList);    // 주문 생성
        orderRepository.save(order);    // 주문 저장

        return order.getId();   // 주문번호 반환
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);   // 주문 목록 조회
        Long totalCount = orderRepository.countOrder(email);                // 주문 개수 조회

        List<OrderHistDto> orderHistDtoList = new ArrayList<>();            // 주문 정보 리스트 생성

        for(Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);            // 주문 정보 생성
            List<OrderItem> orderItems = order.getOrderItems();             // 주문 상품 목록 조회
            for(OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");    // 대표 이미지 조회
                OrderItemDto  orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());    // 주문 상품 정보 생성
                orderHistDto.addOrderItemDto(orderItemDto);                 // 주문 상품 정보 추가
            }
            orderHistDtoList.add(orderHistDto);                             // 주문 정보 추가
        }
        return new PageImpl<OrderHistDto>(orderHistDtoList, pageable, totalCount);    // 페이지 구현 객체를 생성하여 반환
    }

    /**
     * 주문에 대한 검증을 수행 (주문번호와 회원 이메일을 이용하여 검증)
     * @param orderId 주문번호
     * @param email 회원 이메일
     * @return 주문 정보
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member curMember = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);   // 로그인 회원 조회
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);          // 주문 조회
        Member savedMember = order.getMember();    // 주문 회원 조회

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {    // 주문 회원과 로그인 회원 비교
            return false;    // 주문 회원과 로그인 회원이 다르면 false 반환
        }
        return true;    // 주문 회원과 로그인 회원이 같으면 true 반환
    }

    /**
     * 주문 취소<br>
     * 주문 취소 상태로 변경하면 변경 감지 기능에 의해서 트랜잭션이 끝날 때 업데이트 쿼리 실행
     * @param orderId 주문번호
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);    // 주문 조회
        order.cancelOrder();    // 주문 취소
    }
}
