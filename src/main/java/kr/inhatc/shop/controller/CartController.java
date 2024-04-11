package kr.inhatc.shop.controller;

import jakarta.validation.Valid;
import kr.inhatc.shop.dto.CartItemDto;
import kr.inhatc.shop.service.CartService;
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

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                                    BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();                             // 에러 메시지를 담을 StringBuilder
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();      // 에러 필드 목록을 가져온다
            for(FieldError fieldError : fieldErrors){                           // 에러 필드 목록을 순회하며
                sb.append(fieldError.getDefaultMessage());                      // 에러 메시지를 StringBuilder에 추가한다
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);   // 에러 메시지와 함께 400 에러를 반환한다
        }

        String email = principal.getName();     // 로그인한 사용자의 이메일을 가져온다
        Long cartItemId;                        // 장바구니에 담긴 상품 ID를 담을 변수

        try {
            cartItemId = cartService.addCart(cartItemDto, email);   // 장바구니에 상품을 담는다
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);  // 에러 메시지와 함께 400 에러를 반환한다
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK); // 장바구니에 담긴 상품 ID와 함께 200 성공을 반환한다
    }

}
