package kr.inhatc.shop.controller;

import jakarta.validation.Valid;
import kr.inhatc.shop.dto.OrderDto;
import kr.inhatc.shop.dto.OrderHistDto;
import kr.inhatc.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * 주문 관련 요청을 처리하기 위한 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {
        // 주문 정보 유효성 검사
        if(bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();                             // 에러 메시지를 담을 StringBuilder
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();      // 에러 필드 목록
            for(FieldError fieldError : fieldErrors) {                          // 에러 필드 목록을 순회하며
                sb.append(fieldError.getDefaultMessage());                      // 에러 메시지를 StringBuilder에 추가
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   // 에러 메시지와 함께 400 에러 반환
        }
        String email = principal.getName();         // 로그인한 사용자의 이메일을 가져옴
        Long orderId;                               // 주문번호를 담을 변수

        try {
            orderId = orderService.order(orderDto, email);    // 주문 로직 실행
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);   // 주문에 실패한 경우 400 에러 반환
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);    // 주문에 성공한 경우 주문 번호와 200 성공 반환
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);    // 페이지 정보 생성
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);    // 주문 목록 조회

        model.addAttribute("orders", orderHistDtoList);         // 주문 목록을 모델에 추가
        model.addAttribute("page", pageable.getPageNumber());   // 현재 페이지 번호를 모델에 추가
        model.addAttribute("maxPage", 5);    // 최대 페이지 번호를 모델에 추가

        return "order/orderHist";
    }

    /**
     * 주문 취소 요청 처리
     * @param orderId 주문번호
     * @param principal 로그인한 사용자 정보
     * @return 주문 취소 결과
     */
    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        if(!orderService.validateOrder(orderId, principal.getName())) {    // 주문 취소 유효성 검사
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);    // 주문 취소 권한이 없는 경우 403 에러 반환
        }
        orderService.cancelOrder(orderId);                          // 주문 취소 로직 실행
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);    // 주문 취소에 성공한 경우 200 성공 반환
    }
}
