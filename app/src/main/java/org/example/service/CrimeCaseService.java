package org.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.entity.CaseDifficulty;
import org.example.entity.CaseParticipation;
import org.example.entity.CasePrivacy;
import org.example.entity.CaseStatus;
import org.example.entity.CaseType;
import org.example.entity.CrimeCase;
import org.example.entity.ParticipationRole;
import org.example.entity.ParticipationStatus;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.repository.CaseParticipationRepository;
import org.example.repository.CrimeCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrimeCaseService {
    
    @Autowired
    private CrimeCaseRepository crimeCaseRepository;
    
    @Autowired
    private CaseParticipationRepository caseParticipationRepository;
    
    @Autowired
    private UserService userService;
    
    // Basic CRUD operations
    public CrimeCase createCase(CrimeCase crimeCase, User postedBy) {
        // Validate organization can post cases
        if (postedBy.getRole() != UserRole.ORGANIZATION) {
            throw new IllegalArgumentException("Only organizations can post cases");
        }
        
        if (!postedBy.isOrganizationVerified()) {
            throw new IllegalArgumentException("Organization must be verified to post cases");
        }
        
        // Set case properties
        crimeCase.setPostedBy(postedBy);
        crimeCase.setStatus(CaseStatus.OPEN);
        crimeCase.setPostedAt(LocalDateTime.now());
        crimeCase.setUpdatedAt(LocalDateTime.now());
        
        CrimeCase savedCase = crimeCaseRepository.save(crimeCase);
        
        // Create participation record for the organization
        CaseParticipation participation = new CaseParticipation();
        participation.setUser(postedBy);
        participation.setCrimeCase(savedCase);
        participation.setRole(ParticipationRole.OWNER);
        participation.setStatus(ParticipationStatus.ACTIVE);
        caseParticipationRepository.save(participation);
        
        return savedCase;
    }
    
    public CrimeCase updateCase(CrimeCase crimeCase) {
        crimeCase.setUpdatedAt(LocalDateTime.now());
        return crimeCaseRepository.save(crimeCase);
    }
    
    public Optional<CrimeCase> findById(Long id) {
        return crimeCaseRepository.findById(id);
    }
    
    public List<CrimeCase> findAll() {
        return crimeCaseRepository.findAll();
    }
    
    public Page<CrimeCase> findAll(Pageable pageable) {
        return crimeCaseRepository.findAll(pageable);
    }
    
    public void deleteCase(Long id) {
        crimeCaseRepository.deleteById(id);
    }
    
    // Case solving operations
    public CrimeCase solveCase(Long caseId, User solver, String solution, String solutionNotes) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            
            // Validate solver can solve this case
            if (!canUserSolveCase(solver, crimeCase)) {
                throw new IllegalArgumentException("User cannot solve this case");
            }
            
            // Update case status
            crimeCase.setStatus(CaseStatus.SOLVED);
            crimeCase.setSolvedBy(solver);
            crimeCase.setSolution(solution);
            crimeCase.setSolutionNotes(solutionNotes);
            crimeCase.setSolvedAt(LocalDateTime.now());
            crimeCase.setUpdatedAt(LocalDateTime.now());
            
            // Update solver's solved cases count
            userService.incrementSolvedCases(solver);
            
            return crimeCaseRepository.save(crimeCase);
        }
        throw new IllegalArgumentException("Case not found");
    }
    
    public CrimeCase closeCase(Long caseId) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            crimeCase.setStatus(CaseStatus.CLOSED);
            crimeCase.setClosedAt(LocalDateTime.now());
            crimeCase.setUpdatedAt(LocalDateTime.now());
            return crimeCaseRepository.save(crimeCase);
        }
        throw new IllegalArgumentException("Case not found");
    }
    
    public CrimeCase reopenCase(Long caseId) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            crimeCase.setStatus(CaseStatus.OPEN);
            crimeCase.setClosedAt(null);
            crimeCase.setUpdatedAt(LocalDateTime.now());
            return crimeCaseRepository.save(crimeCase);
        }
        throw new IllegalArgumentException("Case not found");
    }
    
    // Case assignment operations
    public CrimeCase assignPrimarySolver(Long caseId, User solver) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            
            // Validate solver
            if (solver.getRole() != UserRole.SOLVER) {
                throw new IllegalArgumentException("Only solvers can be assigned to cases");
            }
            
            crimeCase.setPrimarySolver(solver);
            crimeCase.setUpdatedAt(LocalDateTime.now());
            return crimeCaseRepository.save(crimeCase);
        }
        throw new IllegalArgumentException("Case not found");
    }
    
    public CrimeCase addAssignedSolver(Long caseId, User solver) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            
            // Validate solver
            if (solver.getRole() != UserRole.SOLVER) {
                throw new IllegalArgumentException("Only solvers can be assigned to cases");
            }
            
            List<User> assignedSolvers = crimeCase.getAssignedSolvers();
            if (!assignedSolvers.contains(solver)) {
                assignedSolvers.add(solver);
                crimeCase.setAssignedSolvers(assignedSolvers);
                crimeCase.setUpdatedAt(LocalDateTime.now());
                return crimeCaseRepository.save(crimeCase);
            }
            return crimeCase;
        }
        throw new IllegalArgumentException("Case not found");
    }
    
    // Badge awarding
    public CrimeCase awardBadge(Long caseId, String badgeName) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            
            if (crimeCase.getSolvedBy() != null) {
                crimeCase.setBadgeAwarded(true);
                crimeCase.setAwardedBadge(badgeName);
                crimeCase.setBadgeAwardedAt(LocalDateTime.now());
                crimeCase.setUpdatedAt(LocalDateTime.now());
                
                // Add badge to solver
                userService.addBadge(crimeCase.getSolvedBy(), badgeName);
                
                return crimeCaseRepository.save(crimeCase);
            }
        }
        throw new IllegalArgumentException("Case not found or not solved");
    }
    
    // Search and filter operations
    public List<CrimeCase> findByStatus(CaseStatus status) {
        return crimeCaseRepository.findByStatus(status);
    }
    
    public List<CrimeCase> findByCaseType(CaseType caseType) {
        return crimeCaseRepository.findByCaseType(caseType);
    }
    
    public List<CrimeCase> findByDifficulty(CaseDifficulty difficulty) {
        return crimeCaseRepository.findByDifficulty(difficulty);
    }
    
    public List<CrimeCase> findByPrivacy(CasePrivacy privacy) {
        return crimeCaseRepository.findByPrivacy(privacy);
    }
    
    public List<CrimeCase> findByLocation(String location) {
        return crimeCaseRepository.findByLocationContainingIgnoreCase(location);
    }
    
    public List<CrimeCase> findByPostedBy(User postedBy) {
        return crimeCaseRepository.findByPostedBy(postedBy);
    }
    
    public List<CrimeCase> findBySolvedBy(User solvedBy) {
        return crimeCaseRepository.findBySolvedBy(solvedBy);
    }
    
    public List<CrimeCase> searchByTitleOrDescription(String searchTerm) {
        return crimeCaseRepository.searchByTitleOrDescription(searchTerm);
    }
    
    public List<CrimeCase> findByTag(String tag) {
        return crimeCaseRepository.findByTag(tag);
    }
    
    // Combined filters
    public List<CrimeCase> findByCaseTypeAndDifficulty(CaseType caseType, CaseDifficulty difficulty) {
        return crimeCaseRepository.findByCaseTypeAndDifficulty(caseType, difficulty);
    }
    
    public List<CrimeCase> findByCaseTypeAndStatus(CaseType caseType, CaseStatus status) {
        return crimeCaseRepository.findByCaseTypeAndStatus(caseType, status);
    }
    
    public List<CrimeCase> findByDifficultyAndStatus(CaseDifficulty difficulty, CaseStatus status) {
        return crimeCaseRepository.findByDifficultyAndStatus(difficulty, status);
    }
    
    // Pagination
    public Page<CrimeCase> findRecentCases(Pageable pageable) {
        return crimeCaseRepository.findRecentCases(pageable);
    }
    
    public Page<CrimeCase> findByPostedBy(User postedBy, Pageable pageable) {
        return crimeCaseRepository.findByPostedBy(postedBy, pageable);
    }
    
    public Page<CrimeCase> findByPrivacy(CasePrivacy privacy, Pageable pageable) {
        return crimeCaseRepository.findByPrivacy(privacy, pageable);
    }
    
    // Statistics
    public List<Object[]> getCaseStatisticsByStatus() {
        return crimeCaseRepository.countByStatus();
    }
    
    public List<Object[]> getCaseStatisticsByType() {
        return crimeCaseRepository.countByCaseType();
    }
    
    // Date range queries
    public List<CrimeCase> findByPostedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return crimeCaseRepository.findByPostedAtBetween(startDate, endDate);
    }
    
    public List<CrimeCase> findByIncidentDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return crimeCaseRepository.findByIncidentDateBetween(startDate, endDate);
    }
    
    // Badge-related queries
    public List<CrimeCase> findSolvedCasesWithoutBadge() {
        return crimeCaseRepository.findSolvedCasesWithoutBadge();
    }
    
    public List<CrimeCase> findByBadgeAwarded(boolean badgeAwarded) {
        return crimeCaseRepository.findByBadgeAwarded(badgeAwarded);
    }
    
    // Validation methods
    private boolean canUserSolveCase(User user, CrimeCase crimeCase) {
        // Check if user is a solver
        if (user.getRole() != UserRole.SOLVER) {
            return false;
        }
        
        // Check if case is open or in progress
        if (crimeCase.getStatus() != CaseStatus.OPEN && crimeCase.getStatus() != CaseStatus.IN_PROGRESS) {
            return false;
        }
        
        // Check if user is participating in the case
        return caseParticipationRepository.existsByUserAndCrimeCaseAndStatus(
            user, crimeCase, ParticipationStatus.ACTIVE);
    }
    
    // Case status transitions
    public CrimeCase startCase(Long caseId) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            if (crimeCase.getStatus() == CaseStatus.OPEN) {
                crimeCase.setStatus(CaseStatus.IN_PROGRESS);
                crimeCase.setUpdatedAt(LocalDateTime.now());
                return crimeCaseRepository.save(crimeCase);
            }
        }
        throw new IllegalArgumentException("Case not found or cannot be started");
    }
    
    public CrimeCase pauseCase(Long caseId) {
        Optional<CrimeCase> caseOpt = crimeCaseRepository.findById(caseId);
        if (caseOpt.isPresent()) {
            CrimeCase crimeCase = caseOpt.get();
            if (crimeCase.getStatus() == CaseStatus.IN_PROGRESS) {
                crimeCase.setStatus(CaseStatus.OPEN);
                crimeCase.setUpdatedAt(LocalDateTime.now());
                return crimeCaseRepository.save(crimeCase);
            }
        }
        throw new IllegalArgumentException("Case not found or cannot be paused");
    }
} 