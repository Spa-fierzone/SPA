package com.spazone.service;

import com.spazone.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BranchService {
    // Get all branches
    List<Branch> getAllBranches();

    // Find branch by ID
    Optional<Branch> findById(Integer branchId);

    // Get branch by ID - MISSING METHOD FOR BRANCH ADMIN CONTROLLER
    Branch getById(Integer branchId);

    // Search branches with filters - MISSING METHOD FOR BRANCH ADMIN CONTROLLER
    Page<Branch> searchBranches(String keyword, Integer managerId, Pageable pageable);

    // Additional common methods
    Branch save(Branch branch);

    void deleteById(Integer branchId);

    List<Branch> findAll();

    // Toggle branch status - MISSING METHOD FOR BRANCH ADMIN CONTROLLER
    void toggleStatus(Integer branchId);

    // Find all active branches
    List<Branch> findAllActive();
}
