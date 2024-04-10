package kr.inhatc.shop.repository;

import kr.inhatc.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 회원 이메일로 주문 목록 조회
     * @param email
     * @param pageable
     * @return
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.member.email = :email " +
            "ORDER BY o.orderDate DESC")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /**
     * 회원의 주문 개수가 몇 개인지 조회
     * @param email
     * @return
     */
    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.member.email = :email ")
    Long countOrder(@Param("email") String email);

}
