package org.example.repository;

import org.example.entity.CaseDifficulty;
import org.example.entity.CasePrivacy;
import org.example.entity.CaseStatus;
import org.example.entity.CaseType;
import org.example.entity.CrimeCase;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CrimeCaseRepository extends JpaRepository<CrimeCase, Long> {
    
    // Basic find operations
    List<CrimeCase> findByPostedBy(User postedBy);
    List<CrimeCase> findBySolvedBy(User solvedBy);
    
    // Status-based queries
    List<CrimeCase> findByStatus(CaseStatus status);
    List<CrimeCase> findByStatusIn(List<CaseStatus> statuses);
    
    // Type-based queries
    List<CrimeCase> findByCaseType(CaseType caseType);
    List<CrimeCase> findByCaseTypeIn(List<CaseType> caseTypes);
    
    // Difficulty-based queries
    List<CrimeCase> findByDifficulty(CaseDifficulty difficulty);
    List<CrimeCase> findByDifficultyIn(List<CaseDifficulty> difficulties);
    
    // Privacy-based queries
    List<CrimeCase> findByPrivacy(CasePrivacy privacy);
    List<CrimeCase> findByPrivacyIn(List<CasePrivacy> privacies);
    
    // Location-based queries
    List<CrimeCase> findByLocationContainingIgnoreCase(String location);
    
    // Date-based queries
    List<CrimeCase> findByPostedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<CrimeCase> findByIncidentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Combined queries
    List<CrimeCase> findByCaseTypeAndDifficulty(CaseType caseType, CaseDifficulty difficulty);
    List<CrimeCase> findByCaseTypeAndStatus(CaseType caseType, CaseStatus status);
    List<CrimeCase> findByDifficultyAndStatus(CaseDifficulty difficulty, CaseStatus status);
    
    // Organization-specific queries
    List<CrimeCase> findByPostedByAndStatus(User postedBy, CaseStatus status);
    List<CrimeCase> findByPostedByAndCaseType(User postedBy, CaseType caseType);
    
    // Solver-specific queries
    List<CrimeCase> findBySolvedByAndStatus(User solvedBy, CaseStatus status);
    
    // Badge-related queries
    List<CrimeCase> findByBadgeAwarded(boolean badgeAwarded);
    List<CrimeCase> findByBadgeAwardedAndAwardedBadge(boolean badgeAwarded, String badge);
    
    // Search by title or description
    @Query("SELECT c FROM CrimeCase c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CrimeCase> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    // Find cases by tags
    @Query("SELECT c FROM CrimeCase c JOIN c.tags t WHERE t = :tag")
    List<CrimeCase> findByTag(@Param("tag") String tag);
    
    // Find recent cases
    @Query("SELECT c FROM CrimeCase c ORDER BY c.postedAt DESC")
    Page<CrimeCase> findRecentCases(Pageable pageable);
    
    // Find cases by organization with pagination
    Page<CrimeCase> findByPostedBy(User postedBy, Pageable pageable);
    
    // Find public cases with pagination
    Page<CrimeCase> findByPrivacy(CasePrivacy privacy, Pageable pageable);
    
    // Count cases by status for statistics
    @Query("SELECT c.status, COUNT(c) FROM CrimeCase c GROUP BY c.status")
    List<Object[]> countByStatus();
    
    // Count cases by type for statistics
    @Query("SELECT c.caseType, COUNT(c) FROM CrimeCase c GROUP BY c.caseType")
    List<Object[]> countByCaseType();
    
    // Find cases that need badge awarding (solved but no badge)
    @Query("SELECT c FROM CrimeCase c WHERE c.status = 'SOLVED' AND c.badgeAwarded = false")
    List<CrimeCase> findSolvedCasesWithoutBadge();
} 