package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.entity.HiringPost;
import org.example.entity.HiringPostStatus;
import org.example.repository.HiringPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HiringPostService {
    @Autowired
    private HiringPostRepository hiringPostRepository;

    public HiringPost save(HiringPost post) {
        return hiringPostRepository.save(post);
    }

    public Optional<HiringPost> findById(Long id) {
        return hiringPostRepository.findById(id);
    }

    public List<HiringPost> findAll() {
        return hiringPostRepository.findAll();
    }

    public List<HiringPost> findByRecruiterId(Long recruiterId) {
        return hiringPostRepository.findByRecruiterId(recruiterId);
    }

    public List<HiringPost> findByStatus(HiringPostStatus status) {
        return hiringPostRepository.findByStatus(status);
    }

    public List<HiringPost> findByCaseType(String caseType) {
        return hiringPostRepository.findByCaseType(caseType);
    }

    public List<HiringPost> findByLocation(String location) {
        return hiringPostRepository.findByLocation(location);
    }

    public void deleteById(Long id) {
        hiringPostRepository.deleteById(id);
    }
} 