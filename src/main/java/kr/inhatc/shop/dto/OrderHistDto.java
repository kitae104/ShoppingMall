package kr.inhatc.shop.dto;

import kr.inhatc.shop.constant.OrderStatus;
import kr.inhatc.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 담을 DTO
 */
@Getter @Setter
public class OrderHistDto {

    private Long orderId;               //주문아이디
    private String orderDate;           //주문날짜
    private OrderStatus orderStatus;    //주문 상태

    /**
     * OrderHistDto 생성자
     * Order Entity를 OrderHistDto로 변환
     * @param order
     */
    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));  // 포맷 설정
        this.orderStatus = order.getOrderStatus();
    }

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();        //주문 상품 리스트

    /**
     * 주문 상품 리스트에 주문 상품 추가
     * @param orderItemDto
     */
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
