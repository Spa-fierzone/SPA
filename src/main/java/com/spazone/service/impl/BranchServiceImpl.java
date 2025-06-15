package com.spazone.service.impl;

import com.spazone.entity.Branch;
import com.spazone.repository.BranchRepository;
import com.spazone.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public Page<Branch> searchBranches(String keyword, Integer managerId, Pageable pageable) {
        return branchRepository.searchBranches(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword : null,
                managerId,
                pageable
        );
    }

    @Override
    public Branch getById(Integer id) {
        return branchRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Branch branch) {
        if (branch.getBranchId() != null) {
            branchRepository.findById(branch.getBranchId())
                    .ifPresent(existing -> branch.setCreatedAt(existing.getCreatedAt()));
        }
        branch.setUpdatedAt(LocalDateTime.now());
        branchRepository.save(branch);
    }

    @Override
    public void toggleStatus(Integer id) {
        Branch branch = branchRepository.findById(id).orElse(null);
        if (branch != null) {
            String current = branch.getStatus();
            switch (current) {
                case "active" -> branch.setStatus("inactive");
                case "inactive" -> branch.setStatus("active");
            }
            branch.setUpdatedAt(LocalDateTime.now());
            branchRepository.save(branch);
        }
    }

    @Override
    public List<Branch> findAllActive() {
        return branchRepository.findByIsActiveTrue();
    }
}
