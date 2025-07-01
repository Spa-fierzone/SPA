package com.spazone.service;

import com.spazone.entity.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BranchService {
    Page<Branch> searchBranches(String keyword, Integer managerId, Pageable pageable);
    Branch getById(Integer id);
    void  save(Branch branch);
    void toggleStatus(Integer id);
    List<Branch> findAllActive();

}

