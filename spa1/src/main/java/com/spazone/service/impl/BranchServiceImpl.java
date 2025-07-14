package com.spazone.service.impl;

import com.spazone.entity.Branch;
import com.spazone.exception.ResourceNotFoundException;
import com.spazone.repository.BranchRepository;
import com.spazone.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    @Override
    public Optional<Branch> findById(Integer branchId) {
        return branchRepository.findById(branchId);
    }

    @Override
    public Branch save(Branch branch) {
        if (branch.getBranchId() != null) {
            branchRepository.findById(branch.getBranchId())
                    .ifPresent(existing -> branch.setCreatedAt(existing.getCreatedAt()));
        }
        branch.setUpdatedAt(LocalDateTime.now());
        return branchRepository.save(branch);
    }

    @Override
    public void deleteById(Integer branchId) {
        branchRepository.deleteById(branchId);
    }

    @Override
    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    @Override
    public Branch getById(Integer branchId) {
        return branchRepository.findById(branchId).orElse(null);
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
