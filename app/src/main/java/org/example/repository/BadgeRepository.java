package org.example.repository;

import org.example.entity.Badge;
import org.example.entity.BadgeTier;
import org.example.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    // Basic find operations
    Optional<Badge> findByName(String name);
    Optional<Badge> findByDisplayName(String displayName);
    
    // Type-based queries
    List<Badge> findByType(BadgeType type);
    List<Badge> findByTypeIn(List<BadgeType> types);
    
    // Tier-based queries
    List<Badge> findByTier(BadgeTier tier);
    List<Badge> findByTierIn(List<BadgeTier> tiers);
    
    // Combined type and tier queries
    List<Badge> findByTypeAndTier(BadgeType type, BadgeTier tier);
    
    // Active badges
    List<Badge> findByActive(boolean active);
    List<Badge> findByTypeAndActive(BadgeType type, boolean active);
    List<Badge> findByTierAndActive(BadgeTier tier, boolean active);
    
    // Requirement-based queries
    List<Badge> findByRequiredCases(Integer requiredCases);
    List<Badge> findByRequiredCasesLessThanEqual(Integer maxCases);
    List<Badge> findByRequiredCasesGreaterThanEqual(Integer minCases);
    
    List<Badge> findByRequiredRating(Integer requiredRating);
    List<Badge> findByRequiredRatingLessThanEqual(Integer maxRating);
    List<Badge> findByRequiredRatingGreaterThanEqual(Integer minRating);
    
    // Case type specific badges
    List<Badge> findByRequiredCaseType(String requiredCaseType);
    List<Badge> findByRequiredCaseTypeIsNotNull();
    
    // Specialization specific badges
    List<Badge> findByRequiredSpecialization(String requiredSpecialization);
    List<Badge> findByRequiredSpecializationIsNotNull();
    
    // Find badges by requirements
    @Query("SELECT b FROM Badge b WHERE b.requiredCases <= :solvedCases AND b.active = true")
    List<Badge> findEligibleBadgesByCases(@Param("solvedCases") Integer solvedCases);
    
    @Query("SELECT b FROM Badge b WHERE b.requiredRating <= :averageRating AND b.active = true")
    List<Badge> findEligibleBadgesByRating(@Param("averageRating") Double averageRating);
    
    @Query("SELECT b FROM Badge b WHERE b.requiredCaseType = :caseType AND b.active = true")
    List<Badge> findEligibleBadgesByCaseType(@Param("caseType") String caseType);
    
    @Query("SELECT b FROM Badge b WHERE b.requiredSpecialization = :specialization AND b.active = true")
    List<Badge> findEligibleBadgesBySpecialization(@Param("specialization") String specialization);
    
    // Find all active badges
    @Query("SELECT b FROM Badge b WHERE b.active = true ORDER BY b.tier, b.type")
    List<Badge> findAllActiveBadges();
    
    // Find badges by multiple criteria
    @Query("SELECT b FROM Badge b WHERE b.requiredCases <= :solvedCases AND b.requiredRating <= :averageRating AND b.active = true")
    List<Badge> findEligibleBadges(@Param("solvedCases") Integer solvedCases, @Param("averageRating") Double averageRating);
    
    // Find badges for specific case type and solved cases
    @Query("SELECT b FROM Badge b WHERE b.requiredCaseType = :caseType AND b.requiredCases <= :solvedCases AND b.active = true")
    List<Badge> findEligibleBadgesByCaseTypeAndCount(@Param("caseType") String caseType, @Param("solvedCases") Integer solvedCases);
    
    // Find badges by tier order (Bronze to Diamond)
    @Query("SELECT b FROM Badge b WHERE b.active = true ORDER BY CASE b.tier WHEN 'BRONZE' THEN 1 WHEN 'SILVER' THEN 2 WHEN 'GOLD' THEN 3 WHEN 'PLATINUM' THEN 4 WHEN 'DIAMOND' THEN 5 END")
    List<Badge> findAllActiveBadgesByTierOrder();
    
    // Count badges by type
    @Query("SELECT b.type, COUNT(b) FROM Badge b WHERE b.active = true GROUP BY b.type")
    List<Object[]> countBadgesByType();
    
    // Count badges by tier
    @Query("SELECT b.tier, COUNT(b) FROM Badge b WHERE b.active = true GROUP BY b.tier")
    List<Object[]> countBadgesByTier();
    
    // Find badges that match user criteria
    @Query("SELECT b FROM Badge b WHERE b.active = true AND " +
           "(:solvedCases IS NULL OR b.requiredCases <= :solvedCases) AND " +
           "(:averageRating IS NULL OR b.requiredRating <= :averageRating) AND " +
           "(:caseType IS NULL OR b.requiredCaseType = :caseType) AND " +
           "(:specialization IS NULL OR b.requiredSpecialization = :specialization)")
    List<Badge> findMatchingBadges(@Param("solvedCases") Integer solvedCases, 
                                   @Param("averageRating") Double averageRating,
                                   @Param("caseType") String caseType,
                                   @Param("specialization") String specialization);
    
    // Check if badge exists by name
    boolean existsByName(String name);
    
    // Find badges with no requirements (automatic badges)
    @Query("SELECT b FROM Badge b WHERE b.requiredCases IS NULL AND b.requiredRating IS NULL AND b.requiredCaseType IS NULL AND b.requiredSpecialization IS NULL AND b.active = true")
    List<Badge> findAutomaticBadges();
} 