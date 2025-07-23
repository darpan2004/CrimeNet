package org.example.repository;

import org.example.entity.HiringApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HiringApplicationRepository extends JpaRepository<HiringApplication, Long> {
    // Custom query methods can be added here
    java.util.List<HiringApplication> findByPostId(Long postId);
    java.util.List<HiringApplication> findByApplicantId(Long applicantId);
    java.util.List<HiringApplication> findByStatus(org.example.entity.ApplicationStatus status);
} 