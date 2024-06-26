package kr.inhatc.shop.entity;

import jakarta.persistence.*;
import kr.inhatc.shop.constant.ItemSellStatus;
import kr.inhatc.shop.dto.ItemFormDto;
import kr.inhatc.shop.exception.OutOfStockException;
import kr.inhatc.shop.utils.audit.BaseEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;             // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;       // 상품명

    @Column(nullable = false)
    private int price;           // 상품 가격

    @Column(nullable = false, name="number")
    private int stockNumber;     // 상품 재고 수량

    @Lob // Large Object - CLOB, BLOB
    @Column(nullable = false)
    private String itemDetail;   // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber; // 주문 후 남은 재고 수량 계산
        if (restStock < 0) {                            // 주문 수량이 재고 수량보다 많은 경우
            throw new OutOfStockException
                    ("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;       // 주문 후 남은 재고 수량을 현재 재고 값으로 설정
    }

    /**
     * 상품 재고 추가
     * @param stockNumber
     */
    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }
}
