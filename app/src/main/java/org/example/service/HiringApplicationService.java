package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.entity.ApplicationStatus;
import org.example.entity.HiringApplication;
import org.example.repository.HiringApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HiringApplicationService {
    @Autowired
    private HiringApplicationRepository hiringApplicationRepository;

    public HiringApplication save(HiringApplication application) {
        return hiringApplicationRepository.save(application);
    }

    public Optional<HiringApplication> findById(Long id) {
        return hiringApplicationRepository.findById(id);
    }

    public List<HiringApplication> findByPostId(Long postId) {
        return hiringApplicationRepository.findByPostId(postId);
    }

    public List<HiringApplication> findByApplicantId(Long applicantId) {
        return hiringApplicationRepository.findByApplicantId(applicantId);
    }

    public List<HiringApplication> findByStatus(ApplicationStatus status) {
        return hiringApplicationRepository.findByStatus(status);
    }

    public void deleteById(Long id) {
        hiringApplicationRepository.deleteById(id);
    }
} 