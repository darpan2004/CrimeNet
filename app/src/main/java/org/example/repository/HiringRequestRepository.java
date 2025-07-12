package org.example.repository;

import org.example.entity.CrimeCase;
import org.example.entity.HiringRequest;
import org.example.entity.HiringStatus;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HiringRequestRepository extends JpaRepository<HiringRequest, Long> {
    
    // Basic find operations
    List<HiringRequest> findByOrganization(User organization);
    List<HiringRequest> findByInvestigator(User investigator);
    List<HiringRequest> findByCrimeCase(CrimeCase crimeCase);
    
    // Status-based queries
    List<HiringRequest> findByStatus(HiringStatus status);
    List<HiringRequest> findByOrganizationAndStatus(User organization, HiringStatus status);
    List<HiringRequest> findByInvestigatorAndStatus(User investigator, HiringStatus status);
    List<HiringRequest> findByCrimeCaseAndStatus(CrimeCase crimeCase, HiringStatus status);
    
    // Date-based queries
    List<HiringRequest> findByRequestedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<HiringRequest> findByRequestedAtGreaterThanEqual(LocalDateTime date);
    List<HiringRequest> findByRespondedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<HiringRequest> findByAcceptedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<HiringRequest> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Rate-based queries
    List<HiringRequest> findByProposedRate(Double rate);
    List<HiringRequest> findByProposedRateGreaterThanEqual(Double minRate);
    List<HiringRequest> findByProposedRateLessThanEqual(Double maxRate);
    List<HiringRequest> findByProposedRateBetween(Double minRate, Double maxRate);
    
    // Combined queries
    List<HiringRequest> findByOrganizationAndStatusIn(User organization, List<HiringStatus> statuses);
    List<HiringRequest> findByInvestigatorAndStatusIn(User investigator, List<HiringStatus> statuses);
    
    // Find pending requests
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.status = 'PENDING'")
    List<HiringRequest> findPendingRequests();
    
    // Find pending requests for an investigator
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.investigator = :investigator AND hr.status = 'PENDING'")
    List<HiringRequest> findPendingRequestsForInvestigator(@Param("investigator") User investigator);
    
    // Find active hiring requests (accepted or in progress)
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.status IN ('ACCEPTED', 'IN_PROGRESS')")
    List<HiringRequest> findActiveHiringRequests();
    
    // Find completed hiring requests
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.status = 'COMPLETED'")
    List<HiringRequest> findCompletedHiringRequests();
    
    // Find recent hiring requests
    @Query("SELECT hr FROM HiringRequest hr ORDER BY hr.requestedAt DESC")
    List<HiringRequest> findRecentHiringRequests();
    
    // Count hiring requests by status
    @Query("SELECT hr.status, COUNT(hr) FROM HiringRequest hr GROUP BY hr.status")
    List<Object[]> countHiringRequestsByStatus();
    
    // Count hiring requests for an organization
    long countByOrganization(User organization);
    long countByOrganizationAndStatus(User organization, HiringStatus status);
    
    // Count hiring requests for an investigator
    long countByInvestigator(User investigator);
    long countByInvestigatorAndStatus(User investigator, HiringStatus status);
    
    // Find hiring requests with responses
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.investigatorResponse IS NOT NULL AND hr.investigatorResponse != ''")
    List<HiringRequest> findHiringRequestsWithResponses();
    
    // Find hiring requests by organization message
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.organizationMessage IS NOT NULL AND hr.organizationMessage != ''")
    List<HiringRequest> findHiringRequestsWithMessages();
    
    // Find hiring requests for a specific case
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.crimeCase = :crimeCase ORDER BY hr.requestedAt DESC")
    List<HiringRequest> findHiringRequestsForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find hiring requests that need response (pending for too long)
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.status = 'PENDING' AND hr.requestedAt < :cutoffDate")
    List<HiringRequest> findOverdueHiringRequests(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find hiring requests by duration
    List<HiringRequest> findByProposedDuration(String duration);
    
    // Find hiring requests with specific requirements
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.requirements IS NOT NULL AND hr.requirements != ''")
    List<HiringRequest> findHiringRequestsWithRequirements();
    
    // Check if hiring request exists
    boolean existsByOrganizationAndInvestigatorAndCrimeCase(User organization, User investigator, CrimeCase crimeCase);
    
    // Find hiring requests in a specific time period
    @Query("SELECT hr FROM HiringRequest hr WHERE hr.requestedAt BETWEEN :startDate AND :endDate")
    List<HiringRequest> findHiringRequestsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find top investigators by hiring requests
    @Query("SELECT hr.investigator, COUNT(hr) as requestCount FROM HiringRequest hr WHERE hr.status = 'COMPLETED' GROUP BY hr.investigator ORDER BY requestCount DESC")
    List<Object[]> findTopInvestigatorsByCompletedRequests();
} 