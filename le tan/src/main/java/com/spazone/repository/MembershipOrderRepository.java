package com.spazone.repository;

import com.spazone.entity.MembershipOrder;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipOrderRepository extends JpaRepository<MembershipOrder, Integer> {

    Optional<MembershipOrder> findByOrderCode(Long orderCode);

    List<MembershipOrder> findByCustomerOrderByCreatedAtDesc(User customer);

    List<MembershipOrder> findByStatus(MembershipOrder.OrderStatus status);

    @Query("SELECT mo FROM MembershipOrder mo WHERE mo.status = 'PENDING' AND mo.createdAt < ?1")
    List<MembershipOrder> findExpiredPendingOrders(LocalDateTime expiredBefore);

    @Query("SELECT COUNT(mo) FROM MembershipOrder mo WHERE mo.customer = ?1 AND mo.status = 'PAID'")
    Long countPaidOrdersByCustomer(User customer);
}