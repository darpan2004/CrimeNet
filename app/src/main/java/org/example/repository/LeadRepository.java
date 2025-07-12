package org.example.repository;

import org.example.entity.CrimeCase;
import org.example.entity.Lead;
import org.example.entity.LeadStatus;
import org.example.entity.LeadType;
import org.example.entity.LeadVisibility;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    // Basic find operations
    List<Lead> findByCrimeCase(CrimeCase crimeCase);
    List<Lead> findBySubmittedBy(User submittedBy);
    List<Lead> findByValidatedBy(User validatedBy);
    
    // Status-based queries
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByCrimeCaseAndStatus(CrimeCase crimeCase, LeadStatus status);
    List<Lead> findBySubmittedByAndStatus(User submittedBy, LeadStatus status);
    
    // Type-based queries
    List<Lead> findByType(LeadType type);
    List<Lead> findByCrimeCaseAndType(CrimeCase crimeCase, LeadType type);
    List<Lead> findBySubmittedByAndType(User submittedBy, LeadType type);
    
    // Visibility-based queries
    List<Lead> findByVisibility(LeadVisibility visibility);
    List<Lead> findByCrimeCaseAndVisibility(CrimeCase crimeCase, LeadVisibility visibility);
    List<Lead> findBySubmittedByAndVisibility(User submittedBy, LeadVisibility visibility);
    
    // Date-based queries
    List<Lead> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Lead> findBySubmittedAtGreaterThanEqual(LocalDateTime date);
    List<Lead> findByValidatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Lead> findByValidatedAtGreaterThanEqual(LocalDateTime date);
    
    // Combined queries
    List<Lead> findByCrimeCaseAndStatusAndVisibility(CrimeCase crimeCase, LeadStatus status, LeadVisibility visibility);
    List<Lead> findBySubmittedByAndStatusAndVisibility(User submittedBy, LeadStatus status, LeadVisibility visibility);
    
    // Find validated leads
    @Query("SELECT l FROM Lead l WHERE l.status = 'VALIDATED'")
    List<Lead> findValidatedLeads();
    
    // Find pending leads
    @Query("SELECT l FROM Lead l WHERE l.status = 'PENDING'")
    List<Lead> findPendingLeads();
    
    // Find rejected leads
    @Query("SELECT l FROM Lead l WHERE l.status = 'REJECTED'")
    List<Lead> findRejectedLeads();
    
    // Find investigating leads
    @Query("SELECT l FROM Lead l WHERE l.status = 'INVESTIGATING'")
    List<Lead> findInvestigatingLeads();
    
    // Find public leads for a case
    @Query("SELECT l FROM Lead l WHERE l.crimeCase = :crimeCase AND l.visibility = 'PUBLIC' ORDER BY l.submittedAt DESC")
    List<Lead> findPublicLeadsForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find private leads for a user
    @Query("SELECT l FROM Lead l WHERE l.submittedBy = :user AND l.visibility = 'PRIVATE' ORDER BY l.submittedAt DESC")
    List<Lead> findPrivateLeadsForUser(@Param("user") User user);
    
    // Find recent leads
    @Query("SELECT l FROM Lead l ORDER BY l.submittedAt DESC")
    List<Lead> findRecentLeads();
    
    // Find leads with comments
    @Query("SELECT l FROM Lead l WHERE SIZE(l.comments) > 0")
    List<Lead> findLeadsWithComments();
    
    // Find leads with reactions
    @Query("SELECT l FROM Lead l WHERE SIZE(l.reactions) > 0")
    List<Lead> findLeadsWithReactions();
    
    // Find leads by tags
    @Query("SELECT l FROM Lead l JOIN l.tags t WHERE t = :tag")
    List<Lead> findByTag(@Param("tag") String tag);
    
    // Find leads with specific tags
    @Query("SELECT l FROM Lead l JOIN l.tags t WHERE t IN :tags")
    List<Lead> findByTags(@Param("tags") List<String> tags);
    
    // Search leads by content
    @Query("SELECT l FROM Lead l WHERE LOWER(l.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Lead> searchByContent(@Param("searchTerm") String searchTerm);
    
    // Count leads
    long countByCrimeCase(CrimeCase crimeCase);
    long countBySubmittedBy(User submittedBy);
    long countByStatus(LeadStatus status);
    long countByType(LeadType type);
    long countByVisibility(LeadVisibility visibility);
    
    // Count leads by status for a case
    @Query("SELECT l.status, COUNT(l) FROM Lead l WHERE l.crimeCase = :crimeCase GROUP BY l.status")
    List<Object[]> countLeadsByStatusForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Count leads by type for a case
    @Query("SELECT l.type, COUNT(l) FROM Lead l WHERE l.crimeCase = :crimeCase GROUP BY l.type")
    List<Object[]> countLeadsByTypeForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find leads that need validation
    @Query("SELECT l FROM Lead l WHERE l.status = 'PENDING' AND l.submittedAt < :cutoffDate")
    List<Lead> findLeadsNeedingValidation(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find leads with file attachments
    @Query("SELECT l FROM Lead l WHERE l.filePath IS NOT NULL AND l.filePath != ''")
    List<Lead> findLeadsWithFiles();
    
    // Find leads by file type
    @Query("SELECT l FROM Lead l WHERE l.fileType = :fileType")
    List<Lead> findByFileType(@Param("fileType") String fileType);
    
    // Find leads by file size range
    @Query("SELECT l FROM Lead l WHERE l.fileSize BETWEEN :minSize AND :maxSize")
    List<Lead> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
    
    // Check if lead exists
    boolean existsBySubmittedByAndCrimeCase(User submittedBy, CrimeCase crimeCase);
    
    // Find leads in a specific time period
    @Query("SELECT l FROM Lead l WHERE l.submittedAt BETWEEN :startDate AND :endDate")
    List<Lead> findLeadsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find top submitters
    @Query("SELECT l.submittedBy, COUNT(l) as leadCount FROM Lead l WHERE l.status = 'VALIDATED' GROUP BY l.submittedBy ORDER BY leadCount DESC")
    List<Object[]> findTopSubmitters();
    
    // Find leads with highest reaction count
    @Query("SELECT l, SIZE(l.reactions) as reactionCount FROM Lead l ORDER BY reactionCount DESC")
    List<Object[]> findLeadsByReactionCount();
} 