package com.spazone.repository;

import com.spazone.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    List<Membership> findByStatusOrderByPriceAsc(String status);

    List<Membership> findByStatusAndPriceBetweenOrderByPriceAsc(String status,
                                                                java.math.BigDecimal minPrice,
                                                                java.math.BigDecimal maxPrice);

    @Query("SELECT m FROM Membership m WHERE m.status = 'active' ORDER BY m.price ASC")
    List<Membership> findActiveMembershipsOrderByPrice();

    @Query("SELECT m FROM Membership m WHERE m.status = 'active' AND m.durationMonths = ?1")
    List<Membership> findActiveMembershipsByDuration(Integer durationMonths);
}