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

    @Query("SELECT b FROM Branch b " +
            "WHERE (:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:managerId IS NULL OR b.managerId = :managerId)")
    Page<Branch> searchBranches(@Param("keyword") String keyword,
                                @Param("managerId") Integer managerId,
                                Pageable pageable);

    List<Branch> findByIsActiveTrue();

}
