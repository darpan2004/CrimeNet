package org.example.repository;

import org.example.entity.HiringPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HiringPostRepository extends JpaRepository<HiringPost, Long> {
    // Custom query methods can be added here
    java.util.List<HiringPost> findByRecruiterId(Long recruiterId);
    java.util.List<HiringPost> findByStatus(org.example.entity.HiringPostStatus status);
    java.util.List<HiringPost> findByCaseType(String caseType);
    java.util.List<HiringPost> findByLocation(String location);
} 