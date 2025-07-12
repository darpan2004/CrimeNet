package org.example.service;

import org.example.entity.*;
import org.example.repository.HiringRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HiringRequestService {
    
    @Autowired
    private HiringRequestRepository hiringRequestRepository;
    
    @Autowired
    private UserService userService;
    
    // Create hiring request
    public HiringRequest createHiringRequest(User organization, User investigator, 
                                           CrimeCase crimeCase, String title, String description,
                                           Double proposedRate, String proposedDuration,
                                           String requirements, String contactInfo) {
        // Validate organization can hire
        if (organization.getRole() != UserRole.ORGANIZATION) {
            throw new IllegalArgumentException("Only organizations can create hiring requests");
        }
        
        if (!organization.isOrganizationVerified()) {
            throw new IllegalArgumentException("Organization must be verified to hire investigators");
        }
        
        // Validate investigator can be hired
        if (investigator.getRole() != UserRole.SOLVER) {
            throw new IllegalArgumentException("Can only hire solvers as investigators");
        }
        
        if (!investigator.isAvailableForHire()) {
            throw new IllegalArgumentException("Investigator is not available for hire");
        }
        
        // Check if there's already a pending request
        boolean existingRequest = hiringRequestRepository.existsByOrganizationAndInvestigatorAndCrimeCase(
            organization, investigator, crimeCase);
        
        if (existingRequest) {
            throw new IllegalArgumentException("There is already a hiring request for this investigator and case");
        }
        
        // Create hiring request
        HiringRequest hiringRequest = new HiringRequest();
        hiringRequest.setOrganization(organization);
        hiringRequest.setInvestigator(investigator);
        hiringRequest.setCrimeCase(crimeCase);
        hiringRequest.setTitle(title);
        hiringRequest.setDescription(description);
        hiringRequest.setProposedRate(proposedRate);
        hiringRequest.setProposedDuration(proposedDuration);
        hiringRequest.setRequirements(requirements);
        hiringRequest.setContactInfo(contactInfo);
        hiringRequest.setStatus(HiringStatus.PENDING);
        hiringRequest.setRequestedAt(LocalDateTime.now());
        
        return hiringRequestRepository.save(hiringRequest);
    }
    
    // Update hiring request
    public HiringRequest updateHiringRequest(HiringRequest hiringRequest) {
        return hiringRequestRepository.save(hiringRequest);
    }
    
    // Accept hiring request
    public HiringRequest acceptHiringRequest(Long requestId, String investigatorResponse) {
        Optional<HiringRequest> requestOpt = hiringRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            HiringRequest request = requestOpt.get();
            
            if (request.getStatus() != HiringStatus.PENDING) {
                throw new IllegalArgumentException("Can only accept pending requests");
            }
            
            request.setStatus(HiringStatus.ACCEPTED);
            request.setInvestigatorResponse(investigatorResponse);
            request.setAcceptedAt(LocalDateTime.now());
            request.setRespondedAt(LocalDateTime.now());
            
            return hiringRequestRepository.save(request);
        }
        throw new IllegalArgumentException("Hiring request not found");
    }
    
    // Reject hiring request
    public HiringRequest rejectHiringRequest(Long requestId, String investigatorResponse) {
        Optional<HiringRequest> requestOpt = hiringRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            HiringRequest request = requestOpt.get();
            
            if (request.getStatus() != HiringStatus.PENDING) {
                throw new IllegalArgumentException("Can only reject pending requests");
            }
            
            request.setStatus(HiringStatus.DECLINED);
            request.setInvestigatorResponse(investigatorResponse);
            request.setRespondedAt(LocalDateTime.now());
            
            return hiringRequestRepository.save(request);
        }
        throw new IllegalArgumentException("Hiring request not found");
    }
    
    // Complete hiring request
    public HiringRequest completeHiringRequest(Long requestId) {
        Optional<HiringRequest> requestOpt = hiringRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            HiringRequest request = requestOpt.get();
            
            if (request.getStatus() != HiringStatus.ACCEPTED) {
                throw new IllegalArgumentException("Can only complete accepted requests");
            }
            
            request.setStatus(HiringStatus.COMPLETED);
            request.setCompletedAt(LocalDateTime.now());
            
            return hiringRequestRepository.save(request);
        }
        throw new IllegalArgumentException("Hiring request not found");
    }
    
    // Find operations
    public Optional<HiringRequest> findById(Long id) {
        return hiringRequestRepository.findById(id);
    }
    
    public List<HiringRequest> findByOrganization(User organization) {
        return hiringRequestRepository.findByOrganization(organization);
    }
    
    public List<HiringRequest> findByInvestigator(User investigator) {
        return hiringRequestRepository.findByInvestigator(investigator);
    }
    
    public List<HiringRequest> findByCrimeCase(CrimeCase crimeCase) {
        return hiringRequestRepository.findByCrimeCase(crimeCase);
    }
    
    public List<HiringRequest> findByStatus(HiringStatus status) {
        return hiringRequestRepository.findByStatus(status);
    }
    
    public List<HiringRequest> findByOrganizationAndStatus(User organization, HiringStatus status) {
        return hiringRequestRepository.findByOrganizationAndStatus(organization, status);
    }
    
    public List<HiringRequest> findByInvestigatorAndStatus(User investigator, HiringStatus status) {
        return hiringRequestRepository.findByInvestigatorAndStatus(investigator, status);
    }
    
    // Get pending requests
    public List<HiringRequest> getPendingRequests() {
        return hiringRequestRepository.findPendingRequests();
    }
    
    public List<HiringRequest> getPendingRequestsForInvestigator(User investigator) {
        return hiringRequestRepository.findPendingRequestsForInvestigator(investigator);
    }
    
    // Get active contracts
    public List<HiringRequest> getActiveHiringRequests() {
        return hiringRequestRepository.findActiveHiringRequests();
    }
    
    public List<HiringRequest> getActiveContracts(User user) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.findByOrganizationAndStatusIn(user, 
                List.of(HiringStatus.ACCEPTED, HiringStatus.IN_PROGRESS));
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.findByInvestigatorAndStatusIn(user, 
                List.of(HiringStatus.ACCEPTED, HiringStatus.IN_PROGRESS));
        }
        return List.of();
    }
    
    // Get completed contracts
    public List<HiringRequest> getCompletedHiringRequests() {
        return hiringRequestRepository.findCompletedHiringRequests();
    }
    
    public List<HiringRequest> getCompletedContracts(User user) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.findByOrganizationAndStatus(user, HiringStatus.COMPLETED);
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.findByInvestigatorAndStatus(user, HiringStatus.COMPLETED);
        }
        return List.of();
    }
    
    // Rate-based queries
    public List<HiringRequest> findByRateRange(Double minRate, Double maxRate) {
        return hiringRequestRepository.findByProposedRateBetween(minRate, maxRate);
    }
    
    public List<HiringRequest> findByMinRate(Double minRate) {
        return hiringRequestRepository.findByProposedRateGreaterThanEqual(minRate);
    }
    
    public List<HiringRequest> findByMaxRate(Double maxRate) {
        return hiringRequestRepository.findByProposedRateLessThanEqual(maxRate);
    }
    
    // Date-based queries
    public List<HiringRequest> findByRequestedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return hiringRequestRepository.findByRequestedAtBetween(startDate, endDate);
    }
    
    public List<HiringRequest> findByAcceptedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return hiringRequestRepository.findByAcceptedAtBetween(startDate, endDate);
    }
    
    public List<HiringRequest> findByCompletedDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return hiringRequestRepository.findByCompletedAtBetween(startDate, endDate);
    }
    
    // Recent requests
    public List<HiringRequest> getRecentHiringRequests() {
        return hiringRequestRepository.findRecentHiringRequests();
    }
    
    public List<HiringRequest> getRecentRequestsForUser(User user) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.findByOrganization(user);
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.findByInvestigator(user);
        }
        return List.of();
    }
    
    // Statistics
    public long getRequestCount(User user) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.countByOrganization(user);
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.countByInvestigator(user);
        }
        return 0;
    }
    
    public long getRequestCountByStatus(User user, HiringStatus status) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.countByOrganizationAndStatus(user, status);
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.countByInvestigatorAndStatus(user, status);
        }
        return 0;
    }
    
    public List<Object[]> getHiringStatistics() {
        return hiringRequestRepository.countHiringRequestsByStatus();
    }
    
    public List<Object[]> getTopInvestigators() {
        return hiringRequestRepository.findTopInvestigatorsByCompletedRequests();
    }
    
    // Check if user can be hired
    public boolean canHireInvestigator(User organization, User investigator) {
        // Check if organization is verified
        if (!organization.isOrganizationVerified()) {
            return false;
        }
        
        // Check if investigator is available
        if (!investigator.isAvailableForHire()) {
            return false;
        }
        
        return true;
    }
    
    // Get hiring history between organization and investigator
    public List<HiringRequest> getHiringHistory(User organization, User investigator) {
        // Get all requests between this organization and investigator
        List<HiringRequest> allRequests = hiringRequestRepository.findByOrganization(organization);
        return allRequests.stream()
            .filter(request -> request.getInvestigator().equals(investigator))
            .toList();
    }
    
    // Get successful hires count
    public long getSuccessfulHiresCount(User user) {
        if (user.getRole() == UserRole.ORGANIZATION) {
            return hiringRequestRepository.countByOrganizationAndStatus(user, HiringStatus.COMPLETED);
        } else if (user.getRole() == UserRole.SOLVER) {
            return hiringRequestRepository.countByInvestigatorAndStatus(user, HiringStatus.COMPLETED);
        }
        return 0;
    }
    
    // Get hiring success rate
    public double getHiringSuccessRate(User user) {
        long totalRequests = getRequestCount(user);
        long successfulRequests = getSuccessfulHiresCount(user);
        
        if (totalRequests == 0) {
            return 0.0;
        }
        
        return (double) successfulRequests / totalRequests * 100;
    }
    
    // Search requests by description
    public List<HiringRequest> searchRequestsByDescription(String searchTerm) {
        // This would need a custom query method in the repository
        // For now, return all requests and filter in memory
        return hiringRequestRepository.findAll().stream()
            .filter(request -> request.getDescription() != null && 
                   request.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }
    
    // Get requests with responses
    public List<HiringRequest> getRequestsWithResponses() {
        return hiringRequestRepository.findHiringRequestsWithResponses();
    }
    
    // Get requests with messages
    public List<HiringRequest> getRequestsWithMessages() {
        return hiringRequestRepository.findHiringRequestsWithMessages();
    }
    
    // Get requests by duration
    public List<HiringRequest> getRequestsByDuration(String duration) {
        return hiringRequestRepository.findByProposedDuration(duration);
    }
    
    // Get requests with requirements
    public List<HiringRequest> getRequestsWithRequirements() {
        return hiringRequestRepository.findHiringRequestsWithRequirements();
    }
    
    // Get overdue requests
    public List<HiringRequest> getOverdueRequests(LocalDateTime cutoffDate) {
        return hiringRequestRepository.findOverdueHiringRequests(cutoffDate);
    }
} 