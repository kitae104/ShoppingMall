package kr.inhatc.shop.controller;

import jakarta.validation.Valid;
import kr.inhatc.shop.dto.OrderDto;
import kr.inhatc.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

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


}
