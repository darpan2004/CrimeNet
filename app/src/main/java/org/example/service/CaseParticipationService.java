package org.example.service;

import org.example.entity.*;
import org.example.repository.CaseParticipationRepository;
import org.example.repository.CrimeCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CaseParticipationService {
    
    @Autowired
    private CaseParticipationRepository caseParticipationRepository;
    
    @Autowired
    private CrimeCaseRepository crimeCaseRepository;
    
    @Autowired
    private UserService userService;
    
    // Join case operations
    public CaseParticipation joinCase(User user, CrimeCase crimeCase, ParticipationRole role) {
        // Validate user can join the case
        if (!canUserJoinCase(user, crimeCase)) {
            throw new IllegalArgumentException("User cannot join this case");
        }
        
        // Check if user is already participating
        Optional<CaseParticipation> existingParticipation = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (existingParticipation.isPresent()) {
            CaseParticipation participation = existingParticipation.get();
            if (participation.getStatus() == ParticipationStatus.ACTIVE) {
                throw new IllegalArgumentException("User is already participating in this case");
            } else {
                // Reactivate participation
                participation.setStatus(ParticipationStatus.ACTIVE);
                participation.setRole(role);
                participation.setLastActivityAt(LocalDateTime.now());
                return caseParticipationRepository.save(participation);
            }
        }
        
        // Create new participation
        CaseParticipation participation = new CaseParticipation();
        participation.setUser(user);
        participation.setCrimeCase(crimeCase);
        participation.setRole(role);
        participation.setStatus(ParticipationStatus.ACTIVE);
        participation.setJoinedAt(LocalDateTime.now());
        participation.setLastActivityAt(LocalDateTime.now());
        
        CaseParticipation savedParticipation = caseParticipationRepository.save(participation);
        
        // Update user's active cases count
        updateUserActiveCasesCount(user);
        
        return savedParticipation;
    }
    
    public CaseParticipation joinCaseAsFollower(User user, CrimeCase crimeCase) {
        return joinCase(user, crimeCase, ParticipationRole.FOLLOWER);
    }
    
    public CaseParticipation joinCaseAsSolver(User user, CrimeCase crimeCase) {
        return joinCase(user, crimeCase, ParticipationRole.SOLVER);
    }
    
    // Leave case operations
    public void leaveCase(User user, CrimeCase crimeCase) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            CaseParticipation participation = participationOpt.get();
            participation.setStatus(ParticipationStatus.INACTIVE);
            participation.setLastActivityAt(LocalDateTime.now());
            caseParticipationRepository.save(participation);
            
            // Update user's active cases count
            updateUserActiveCasesCount(user);
        } else {
            throw new IllegalArgumentException("User is not participating in this case");
        }
    }
    
    public void suspendParticipation(User user, CrimeCase crimeCase) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            CaseParticipation participation = participationOpt.get();
            participation.setStatus(ParticipationStatus.SUSPENDED);
            participation.setLastActivityAt(LocalDateTime.now());
            caseParticipationRepository.save(participation);
            
            // Update user's active cases count
            updateUserActiveCasesCount(user);
        } else {
            throw new IllegalArgumentException("User is not participating in this case");
        }
    }
    
    public void completeParticipation(User user, CrimeCase crimeCase) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            CaseParticipation participation = participationOpt.get();
            participation.setStatus(ParticipationStatus.COMPLETED);
            participation.setLastActivityAt(LocalDateTime.now());
            caseParticipationRepository.save(participation);
            
            // Update user's active cases count
            updateUserActiveCasesCount(user);
        } else {
            throw new IllegalArgumentException("User is not participating in this case");
        }
    }
    
    // Role management
    public CaseParticipation changeRole(User user, CrimeCase crimeCase, ParticipationRole newRole) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            CaseParticipation participation = participationOpt.get();
            
            // Validate role change
            if (!canChangeRole(participation.getRole(), newRole)) {
                throw new IllegalArgumentException("Invalid role change");
            }
            
            participation.setRole(newRole);
            participation.setLastActivityAt(LocalDateTime.now());
            return caseParticipationRepository.save(participation);
        } else {
            throw new IllegalArgumentException("User is not participating in this case");
        }
    }
    
    public CaseParticipation promoteToSolver(User user, CrimeCase crimeCase) {
        return changeRole(user, crimeCase, ParticipationRole.SOLVER);
    }
    
    public CaseParticipation promoteToLeader(User user, CrimeCase crimeCase) {
        return changeRole(user, crimeCase, ParticipationRole.LEADER);
    }
    
    public CaseParticipation demoteToFollower(User user, CrimeCase crimeCase) {
        return changeRole(user, crimeCase, ParticipationRole.FOLLOWER);
    }
    
    // Find operations
    public List<CaseParticipation> findByUser(User user) {
        return caseParticipationRepository.findByUser(user);
    }
    
    public List<CaseParticipation> findByCrimeCase(CrimeCase crimeCase) {
        return caseParticipationRepository.findByCrimeCase(crimeCase);
    }
    
    public List<CaseParticipation> findActiveByUser(User user) {
        return caseParticipationRepository.findByUserAndStatus(user, ParticipationStatus.ACTIVE);
    }
    
    public List<CaseParticipation> findActiveByCrimeCase(CrimeCase crimeCase) {
        return caseParticipationRepository.findByCrimeCaseAndStatus(crimeCase, ParticipationStatus.ACTIVE);
    }
    
    public List<CaseParticipation> findByUserAndRole(User user, ParticipationRole role) {
        return caseParticipationRepository.findByUserAndRole(user, role);
    }
    
    public List<CaseParticipation> findByCrimeCaseAndRole(CrimeCase crimeCase, ParticipationRole role) {
        return caseParticipationRepository.findByCrimeCaseAndRole(crimeCase, role);
    }
    
    public Optional<CaseParticipation> findByUserAndCrimeCase(User user, CrimeCase crimeCase) {
        return caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
    }
    
    // Get active participants
    public List<User> getActiveParticipants(CrimeCase crimeCase) {
        return caseParticipationRepository.findActiveUsersForCase(crimeCase);
    }
    
    public List<CrimeCase> getActiveCases(User user) {
        return caseParticipationRepository.findActiveCasesForUser(user);
    }
    
    // Statistics
    public List<Object[]> getParticipantCountsByRole(CrimeCase crimeCase) {
        return caseParticipationRepository.countParticipantsByRole(crimeCase);
    }
    
    public long getActiveParticipantCount(CrimeCase crimeCase) {
        return caseParticipationRepository.findActiveParticipantsForCase(crimeCase).size();
    }
    
    public long getActiveCaseCount(User user) {
        return caseParticipationRepository.findActiveCasesForUser(user).size();
    }
    
    // Activity tracking
    public void updateLastActivity(User user, CrimeCase crimeCase) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            CaseParticipation participation = participationOpt.get();
            participation.setLastActivityAt(LocalDateTime.now());
            caseParticipationRepository.save(participation);
        }
    }
    
    // Validation methods
    private boolean canUserJoinCase(User user, CrimeCase crimeCase) {
        // Check if case is open or in progress
        if (crimeCase.getStatus() != CaseStatus.OPEN && crimeCase.getStatus() != CaseStatus.IN_PROGRESS) {
            return false;
        }
        
        // Check if user is a solver or organization
        if (user.getRole() != UserRole.SOLVER && user.getRole() != UserRole.ORGANIZATION) {
            return false;
        }
        
        // Check case privacy
        if (crimeCase.getPrivacy() == CasePrivacy.PRIVATE) {
            // Only invited users can join private cases
            // This would need additional logic for invitations
            return false;
        }
        
        return true;
    }
    
    private boolean canChangeRole(ParticipationRole currentRole, ParticipationRole newRole) {
        // Only allow certain role changes
        if (currentRole == ParticipationRole.OWNER) {
            // Owner cannot change role
            return false;
        }
        
        if (newRole == ParticipationRole.OWNER) {
            // Cannot promote to owner
            return false;
        }
        
        return true;
    }
    
    private void updateUserActiveCasesCount(User user) {
        long activeCaseCount = getActiveCaseCount(user);
        userService.updateActiveCasesCount(user, (int) activeCaseCount);
    }
    
    // Check participation status
    public boolean isUserParticipating(User user, CrimeCase crimeCase) {
        return caseParticipationRepository.existsByUserAndCrimeCaseAndStatus(
            user, crimeCase, ParticipationStatus.ACTIVE);
    }
    
    public boolean isUserActiveParticipant(User user, CrimeCase crimeCase) {
        return caseParticipationRepository.existsByUserAndCrimeCaseAndStatus(
            user, crimeCase, ParticipationStatus.ACTIVE);
    }
    
    public ParticipationRole getUserRole(User user, CrimeCase crimeCase) {
        Optional<CaseParticipation> participationOpt = 
            caseParticipationRepository.findByUserAndCrimeCase(user, crimeCase);
        
        if (participationOpt.isPresent()) {
            return participationOpt.get().getRole();
        }
        return null;
    }
    
    // Find inactive participations (for cleanup)
    public List<CaseParticipation> findInactiveParticipations(LocalDateTime cutoffDate) {
        return caseParticipationRepository.findInactiveParticipations(cutoffDate);
    }
    
    // Recent participations
    public List<CaseParticipation> findRecentParticipations() {
        return caseParticipationRepository.findRecentParticipations();
    }
} 