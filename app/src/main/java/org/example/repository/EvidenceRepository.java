package org.example.repository;

import org.example.entity.CrimeCase;
import org.example.entity.Evidence;
import org.example.entity.EvidenceStatus;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    // Basic find operations
    List<Evidence> findByCrimeCase(CrimeCase crimeCase);
    List<Evidence> findByUploadedBy(User uploadedBy);
    List<Evidence> findByCollectedBy(User collectedBy);
    List<Evidence> findByAssignedTo(User assignedTo);
    
    // Status-based queries
    List<Evidence> findByStatus(EvidenceStatus status);
    List<Evidence> findByCrimeCaseAndStatus(CrimeCase crimeCase, EvidenceStatus status);
    List<Evidence> findByAssignedToAndStatus(User assignedTo, EvidenceStatus status);
    List<Evidence> findByCollectedByAndStatus(User collectedBy, EvidenceStatus status);
    
    // Type-based queries
    List<Evidence> findByType(org.example.entity.EvidenceType type);
    List<Evidence> findByCrimeCaseAndType(CrimeCase crimeCase, org.example.entity.EvidenceType type);
    
    // Date-based queries
    List<Evidence> findByUploadedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Evidence> findByCollectedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Evidence> findByAnalyzedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Combined queries
    List<Evidence> findByCrimeCaseAndStatusIn(CrimeCase crimeCase, List<EvidenceStatus> statuses);
    List<Evidence> findByAssignedToAndStatusIn(User assignedTo, List<EvidenceStatus> statuses);
    
    // Find evidence by tags
    @Query("SELECT e FROM Evidence e JOIN e.tags t WHERE t = :tag")
    List<Evidence> findByTag(@Param("tag") String tag);
    
    // Find evidence with specific tags
    @Query("SELECT e FROM Evidence e WHERE :tag MEMBER OF e.tags")
    List<Evidence> findEvidenceWithTag(@Param("tag") String tag);
    
    // Find evidence by linked suspects
    @Query("SELECT e FROM Evidence e JOIN e.linkedSuspects s WHERE s.id = :suspectId")
    List<Evidence> findByLinkedSuspect(@Param("suspectId") Long suspectId);
    
    // Find recent evidence
    @Query("SELECT e FROM Evidence e ORDER BY e.uploadedAt DESC")
    List<Evidence> findRecentEvidence();
    
    // Find evidence by file type
    List<Evidence> findByFileType(String fileType);
    
    // Find evidence by file size range
    @Query("SELECT e FROM Evidence e WHERE e.fileSize BETWEEN :minSize AND :maxSize")
    List<Evidence> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
    
    // Find evidence by description (text search)
    @Query("SELECT e FROM Evidence e WHERE e.description LIKE %:searchTerm% OR e.title LIKE %:searchTerm%")
    List<Evidence> findByDescriptionContaining(@Param("searchTerm") String searchTerm);
    
    // Count evidence by status
    long countByStatus(EvidenceStatus status);
    long countByCrimeCaseAndStatus(CrimeCase crimeCase, EvidenceStatus status);
    long countByAssignedToAndStatus(User assignedTo, EvidenceStatus status);
    
    // Count evidence by type
    long countByType(org.example.entity.EvidenceType type);
    long countByCrimeCaseAndType(CrimeCase crimeCase, org.example.entity.EvidenceType type);
    
    // Find evidence that needs analysis
    @Query("SELECT e FROM Evidence e WHERE e.status = 'COLLECTED' AND e.assignedTo IS NOT NULL")
    List<Evidence> findEvidenceNeedingAnalysis();
    
    // Find evidence assigned to user
    @Query("SELECT e FROM Evidence e WHERE e.assignedTo = :user AND e.status IN ('COLLECTED', 'PROCESSING')")
    List<Evidence> findAssignedEvidenceForUser(@Param("user") User user);
    
    // Find evidence by chain of custody
    @Query("SELECT e FROM Evidence e WHERE e.chainOfCustody LIKE %:searchTerm%")
    List<Evidence> findByChainOfCustodyContaining(@Param("searchTerm") String searchTerm);
    
    // Find evidence with analysis results
    @Query("SELECT e FROM Evidence e WHERE e.analysisResults IS NOT NULL AND e.analysisResults != ''")
    List<Evidence> findEvidenceWithAnalysisResults();
    
    // Find evidence with conclusions
    @Query("SELECT e FROM Evidence e WHERE e.conclusions IS NOT NULL AND e.conclusions != ''")
    List<Evidence> findEvidenceWithConclusions();
    
    // Find evidence by location
    List<Evidence> findByLocation(String location);
    
    // Find evidence by source
    List<Evidence> findBySource(String source);
    
    // Find evidence by notes
    @Query("SELECT e FROM Evidence e WHERE e.notes LIKE %:searchTerm%")
    List<Evidence> findByNotesContaining(@Param("searchTerm") String searchTerm);
    
    // Find evidence by incident date range
    List<Evidence> findByIncidentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find evidence by incident date
    List<Evidence> findByIncidentDate(LocalDateTime incidentDate);
    
    // Find evidence by incident date after
    List<Evidence> findByIncidentDateAfter(LocalDateTime date);
    
    // Find evidence by incident date before
    List<Evidence> findByIncidentDateBefore(LocalDateTime date);
    
    // Statistics queries
    @Query("SELECT e.status, COUNT(e) FROM Evidence e GROUP BY e.status")
    List<Object[]> countEvidenceByStatus();
    
    @Query("SELECT e.type, COUNT(e) FROM Evidence e GROUP BY e.type")
    List<Object[]> countEvidenceByType();
    
    @Query("SELECT e.uploadedBy, COUNT(e) FROM Evidence e GROUP BY e.uploadedBy ORDER BY COUNT(e) DESC")
    List<Object[]> findTopEvidenceUploaders();
    
    @Query("SELECT e.assignedTo, COUNT(e) FROM Evidence e WHERE e.status = 'ANALYZED' GROUP BY e.assignedTo ORDER BY COUNT(e) DESC")
    List<Object[]> findTopEvidenceAnalyzers();
} 