package kr.inhatc.shop.controller;

import jakarta.validation.Valid;
import kr.inhatc.shop.dto.CartDetailDto;
import kr.inhatc.shop.dto.CartItemDto;
import kr.inhatc.shop.dto.CartOrderDto;
import kr.inhatc.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());  // 장바구니 목록을 가져온다
        model.addAttribute("cartItems", cartDetailList);             // 장바구니 목록을 모델에 추가한다
        return "cart/cartList";  // 장바구니 페이지로 이동
    }

    /**
     * 장바구니 상품의 수량만 업데이트<br />
     * 요청된 자원의 일부를 업데이트하기 위해 PATCH 메서드를 사용<br />
     * @param cartItemId    장바구니 상품 ID
     * @param count         수정할 수량
     * @param principal     로그인한 사용자 정보
     * @return
     */
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       int count, Principal principal){
        if(count <= 0){
            return new ResponseEntity<String>("수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST);  // 에러 메시지와 함께 400 에러를 반환한다
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){      // 수정 권한을 확인
            return new ResponseEntity<String>("수정 권한이 없습니다. ", HttpStatus.FORBIDDEN);  // 에러 메시지와 함께 403 에러를 반환한다
        }

        cartService.updateCartItemCount(cartItemId, count);  // 장바구니 상품 수량을 수정한다

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);  // 수정된 장바구니 상품 ID와 함께 200 성공을 반환한다
    }

    /**
     * 장바구니 상품을 삭제<br />
     * 요청된 자원을 삭제하기 위해 DELETE 메서드를 사용<br />
     * @param cartItemId    장바구니 상품 ID
     * @param principal     로그인한 사용자 정보
     * @return
     */
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if(!cartService.validateCartItem(cartItemId, principal.getName())){  // 삭제 권한을 확인
            return new ResponseEntity<String>("수정 권한이 없습니다. ", HttpStatus.FORBIDDEN);  // 에러 메시지와 함께 403 에러를 반환한다
        }

        cartService.deleteCartItem(cartItemId);                     // 장바구니 상품을 삭제한다

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK); // 삭제된 장바구니 상품 ID와 함께 200 성공을 반환한다
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();   // 장바구니 주문 목록 리스트
        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){               // 주문 상품 선택이 없는 경우라면
            return new ResponseEntity<String>("주문할 상품을  선택해 주세요.", HttpStatus.FORBIDDEN);
        }

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){  // 주문 권한이 없는 경우라면
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());    // 주문 로직 호출 결과 생성된 주문 번호 반환

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
