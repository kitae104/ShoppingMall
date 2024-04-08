package kr.inhatc.shop.service;

import jakarta.persistence.EntityNotFoundException;
import kr.inhatc.shop.constant.ItemSellStatus;
import kr.inhatc.shop.constant.OrderStatus;
import kr.inhatc.shop.dto.OrderDto;
import kr.inhatc.shop.entity.Item;
import kr.inhatc.shop.entity.Member;
import kr.inhatc.shop.entity.Order;
import kr.inhatc.shop.entity.OrderItem;
import kr.inhatc.shop.repository.ItemRepository;
import kr.inhatc.shop.repository.MemberRepository;
import kr.inhatc.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test11@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();                     // 상품 저장
        Member member = saveMember();               // 회원 저장

        OrderDto orderDto = new OrderDto();         // 주문 정보 생성
        orderDto.setCount(10);                      // 주문 수량
        orderDto.setItemId(item.getId());           // 상품 ID

        Long orderId = orderService.order(orderDto, member.getEmail()); // 주문
        Order order = orderRepository.findById(orderId)         // 주문 정보 조회
                .orElseThrow(EntityNotFoundException::new);     // 주문 정보가 없을 경우 예외 발생

        List<OrderItem> orderItems = order.getOrderItems();     // 주문 상품 목록 조회

        int totalPrice = orderDto.getCount()*item.getPrice();   // 총 주문 금액 계산

        assertEquals(totalPrice, order.getTotalPrice());        // 총 주문 금액 일치 여부
    }

//    @Test
//    @DisplayName("주문 취소 테스트")
//    public void cancelOrder(){
//        Item item = saveItem();                     // 상품 저장
//        Member member = saveMember();               // 회원 저장
//
//        OrderDto orderDto = new OrderDto();         // 주문 정보 생성
//        orderDto.setCount(10);                      // 주문 수량
//        orderDto.setItemId(item.getId());           // 상품 ID
//        Long orderId = orderService.order(orderDto, member.getEmail()); // 주문
//
//        Order order = orderRepository.findById(orderId)         // 주문 정보 조회
//                .orElseThrow(EntityNotFoundException::new);     // 주문 정보가 없을 경우 예외 발생
//        orderService.cancelOrder(orderId);          // 주문 취소
//
//        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());   // 주문 상태가 취소인지 확인
//        assertEquals(100, item.getStockNumber());       // 상품 재고가 원복되었는지 확인
//    }

}