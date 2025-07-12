package org.example.repository;

import org.example.entity.CaseParticipation;
import org.example.entity.CrimeCase;
import org.example.entity.ParticipationRole;
import org.example.entity.ParticipationStatus;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseParticipationRepository extends JpaRepository<CaseParticipation, Long> {
    
    // Basic find operations
    List<CaseParticipation> findByUser(User user);
    List<CaseParticipation> findByCrimeCase(CrimeCase crimeCase);
    
    // Find specific participation
    Optional<CaseParticipation> findByUserAndCrimeCase(User user, CrimeCase crimeCase);
    
    // Role-based queries
    List<CaseParticipation> findByRole(ParticipationRole role);
    List<CaseParticipation> findByUserAndRole(User user, ParticipationRole role);
    List<CaseParticipation> findByCrimeCaseAndRole(CrimeCase crimeCase, ParticipationRole role);
    
    // Status-based queries
    List<CaseParticipation> findByStatus(ParticipationStatus status);
    List<CaseParticipation> findByUserAndStatus(User user, ParticipationStatus status);
    List<CaseParticipation> findByCrimeCaseAndStatus(CrimeCase crimeCase, ParticipationStatus status);
    
    // Combined role and status queries
    List<CaseParticipation> findByUserAndRoleAndStatus(User user, ParticipationRole role, ParticipationStatus status);
    List<CaseParticipation> findByCrimeCaseAndRoleAndStatus(CrimeCase crimeCase, ParticipationRole role, ParticipationStatus status);
    
    // Date-based queries
    List<CaseParticipation> findByJoinedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<CaseParticipation> findByLastActivityAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find active participants
    @Query("SELECT cp FROM CaseParticipation cp WHERE cp.status = 'ACTIVE'")
    List<CaseParticipation> findActiveParticipations();
    
    // Find active participants for a specific case
    @Query("SELECT cp FROM CaseParticipation cp WHERE cp.crimeCase = :crimeCase AND cp.status = 'ACTIVE'")
    List<CaseParticipation> findActiveParticipantsForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find cases a user is participating in
    @Query("SELECT cp.crimeCase FROM CaseParticipation cp WHERE cp.user = :user AND cp.status = 'ACTIVE'")
    List<CrimeCase> findActiveCasesForUser(@Param("user") User user);
    
    // Find users participating in a case
    @Query("SELECT cp.user FROM CaseParticipation cp WHERE cp.crimeCase = :crimeCase AND cp.status = 'ACTIVE'")
    List<User> findActiveUsersForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Count participants by role for a case
    @Query("SELECT cp.role, COUNT(cp) FROM CaseParticipation cp WHERE cp.crimeCase = :crimeCase AND cp.status = 'ACTIVE' GROUP BY cp.role")
    List<Object[]> countParticipantsByRole(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find recent participations
    @Query("SELECT cp FROM CaseParticipation cp ORDER BY cp.joinedAt DESC")
    List<CaseParticipation> findRecentParticipations();
    
    // Find participations by date range
    List<CaseParticipation> findByJoinedAtGreaterThanEqual(LocalDateTime date);
    List<CaseParticipation> findByLastActivityAtGreaterThanEqual(LocalDateTime date);
    
    // Check if user is participating in case
    boolean existsByUserAndCrimeCase(User user, CrimeCase crimeCase);
    
    // Check if user is actively participating in case
    boolean existsByUserAndCrimeCaseAndStatus(User user, CrimeCase crimeCase, ParticipationStatus status);
    
    // Find completed participations
    List<CaseParticipation> findByStatusAndCrimeCase(ParticipationStatus status, CrimeCase crimeCase);
    
    // Find participations that need status update (inactive for too long)
    @Query("SELECT cp FROM CaseParticipation cp WHERE cp.status = 'ACTIVE' AND cp.lastActivityAt < :cutoffDate")
    List<CaseParticipation> findInactiveParticipations(@Param("cutoffDate") LocalDateTime cutoffDate);
} 