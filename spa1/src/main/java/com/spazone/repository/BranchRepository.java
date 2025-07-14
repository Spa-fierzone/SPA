package com.spazone.repository;

import com.spazone.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    // Search branches with keyword and manager filter - MISSING METHOD FOR BRANCH SERVICE
    @Query("""
    SELECT b FROM Branch b 
    WHERE (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
           OR LOWER(b.address) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:managerId IS NULL OR b.managerId = :managerId)
    ORDER BY b.createdAt DESC
    """)
    Page<Branch> searchBranches(@Param("keyword") String keyword,
                               @Param("managerId") Integer managerId,
                               Pageable pageable);

    // Find all active branches - MISSING METHOD FOR BRANCH SERVICE
    @Query("SELECT b FROM Branch b WHERE b.status = 'active' ORDER BY b.name")
    List<Branch> findByIsActiveTrue();

    // Find branches by status
    List<Branch> findByStatus(String status);

    // Find branches by manager ID
    List<Branch> findByManagerId(Integer managerId);

    // Count branches by status
    @Query("SELECT COUNT(b) FROM Branch b WHERE b.status = :status")
    Long countByStatus(@Param("status") String status);

    // Find branches in a specific city or region
    @Query("SELECT b FROM Branch b WHERE LOWER(b.address) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<Branch> findByCity(@Param("city") String city);
}
