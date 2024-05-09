package kr.inhatc.shop.repository;

import kr.inhatc.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    /**
     * 상품 이미지 리스트 가져오기
     * @param itemId
     * @return
     */
    //@Query("select i from ItemImg i where i.item.id = :itemId order by i.id asc")
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);  // 상품 이미지 리스트 가져오기

    /**
     * 상품의 대표 이미지 찾기
     * @param itemId
     * @param repImgYn
     * @return
     */
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);
}
