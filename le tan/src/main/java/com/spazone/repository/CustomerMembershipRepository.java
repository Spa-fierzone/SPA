package com.spazone.repository;

import com.spazone.entity.CustomerMembership;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerMembershipRepository extends JpaRepository<CustomerMembership, Integer> {

    List<CustomerMembership> findByCustomerOrderByCreatedAtDesc(User customer);

    List<CustomerMembership> findByCustomerAndStatus(User customer, String status);

    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.customer = ?1 AND cm.status = 'active' " +
            "AND cm.startDate <= ?2 AND cm.endDate >= ?2")
    Optional<CustomerMembership> findActiveCustomerMembership(User customer, LocalDateTime now);

    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.status = 'active' AND cm.endDate < ?1")
    List<CustomerMembership> findExpiredMemberships(LocalDateTime now);

    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.status = 'active' " +
            "AND FUNCTION('MONTH', cm.startDate) != FUNCTION('MONTH', ?1) " +
            "AND cm.usedBookingsThisMonth > 0")
    List<CustomerMembership> findMembershipsToResetBookingCount(LocalDateTime now);

    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.customer.email = :email AND cm.status = 'active' ORDER BY cm.startDate DESC")
    Optional<CustomerMembership> findByCustomerEmail(String email);
}