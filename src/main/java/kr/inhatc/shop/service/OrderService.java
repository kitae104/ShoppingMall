package kr.inhatc.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.inhatc.shop.dto.OrderDto;
import kr.inhatc.shop.entity.Item;
import kr.inhatc.shop.entity.Member;
import kr.inhatc.shop.entity.Order;
import kr.inhatc.shop.entity.OrderItem;
import kr.inhatc.shop.repository.ItemRepository;
import kr.inhatc.shop.repository.MemberRepository;
import kr.inhatc.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
