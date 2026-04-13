package com.arthuurdp.e_commerce.modules.order;

import com.arthuurdp.e_commerce.modules.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.product.id = :productId AND o.status = com.arthuurdp.e_commerce.modules.order.enums.OrderStatus.DELIVERED")
    boolean hasPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}