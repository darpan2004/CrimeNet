package org.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.entity.Badge;
import org.example.entity.BadgeAward;
import org.example.entity.CrimeCase;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeAwardRepository extends JpaRepository<BadgeAward, Long> {
    
    // Basic find operations
    List<BadgeAward> findByUser(User user);
    List<BadgeAward> findByBadge(Badge badge);
    List<BadgeAward> findByAwardedBy(User awardedBy);
    List<BadgeAward> findByAwardedByOrderByAwardedAtDesc(User awardedBy);
    List<BadgeAward> findByCrimeCase(CrimeCase crimeCase);
    
    // Find specific award
    Optional<BadgeAward> findByUserAndBadge(User user, Badge badge);
    Optional<BadgeAward> findByUserAndBadgeAndCrimeCase(User user, Badge badge, CrimeCase crimeCase);
    
    // Date-based queries
    List<BadgeAward> findByAwardedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<BadgeAward> findByAwardedAtGreaterThanEqual(LocalDateTime date);
    List<BadgeAward> findByAwardedAtLessThanEqual(LocalDateTime date);
    
    // Find awards for a specific user and case
    List<BadgeAward> findByUserAndCrimeCase(User user, CrimeCase crimeCase);
    
    // Find awards by badge type
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.badge.type = :badgeType")
    List<BadgeAward> findByBadgeType(@Param("badgeType") String badgeType);
    
    // Find awards by badge tier
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.badge.tier = :badgeTier")
    List<BadgeAward> findByBadgeTier(@Param("badgeTier") String badgeTier);
    
    // Find recent awards
    @Query("SELECT ba FROM BadgeAward ba ORDER BY ba.awardedAt DESC")
    List<BadgeAward> findRecentAwards();
    
    // Find awards for a specific time period
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.awardedAt BETWEEN :startDate AND :endDate")
    List<BadgeAward> findAwardsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count awards
    long countByUser(User user);
    long countByBadge(Badge badge);
    long countByAwardedBy(User awardedBy);
    long countByCrimeCase(CrimeCase crimeCase);
    
    // Count awards by badge type for a user
    @Query("SELECT COUNT(ba) FROM BadgeAward ba WHERE ba.user = :user AND ba.badge.type = :badgeType")
    long countByUserAndBadgeType(@Param("user") User user, @Param("badgeType") String badgeType);
    
    // Count awards by badge tier for a user
    @Query("SELECT COUNT(ba) FROM BadgeAward ba WHERE ba.user = :user AND ba.badge.tier = :badgeTier")
    long countByUserAndBadgeTier(@Param("user") User user, @Param("badgeTier") String badgeTier);
    
    // Find users with most badges
    @Query("SELECT ba.user, COUNT(ba) as badgeCount FROM BadgeAward ba GROUP BY ba.user ORDER BY badgeCount DESC")
    List<Object[]> findUsersWithMostBadges();
    
    // Find most awarded badges
    @Query("SELECT ba.badge, COUNT(ba) as awardCount FROM BadgeAward ba GROUP BY ba.badge ORDER BY awardCount DESC")
    List<Object[]> findMostAwardedBadges();
    
    // Find awards with reasons
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.reason IS NOT NULL AND ba.reason != ''")
    List<BadgeAward> findAwardsWithReasons();
    
    // Find awards for a specific case
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.crimeCase = :crimeCase ORDER BY ba.awardedAt DESC")
    List<BadgeAward> findAwardsForCase(@Param("crimeCase") CrimeCase crimeCase);
    
    // Find awards by badge name
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.badge.name = :badgeName")
    List<BadgeAward> findByBadgeName(@Param("badgeName") String badgeName);
    
    // Find awards by badge display name
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.badge.displayName = :displayName")
    List<BadgeAward> findByBadgeDisplayName(@Param("displayName") String displayName);
    
    // Check if user has a specific badge
    boolean existsByUserAndBadge(User user, Badge badge);
    
    // Check if user has a badge by name
    @Query("SELECT COUNT(ba) > 0 FROM BadgeAward ba WHERE ba.user = :user AND ba.badge.name = :badgeName")
    boolean existsByUserAndBadgeName(@Param("user") User user, @Param("badgeName") String badgeName);
    
    // Find awards by reason
    List<BadgeAward> findByReasonContainingIgnoreCase(String reason);
    
    // Find awards for a specific badge and user
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.user = :user AND ba.badge = :badge ORDER BY ba.awardedAt DESC")
    List<BadgeAward> findByUserAndBadgeOrdered(@Param("user") User user, @Param("badge") Badge badge);
    
    // Find awards by badge type and tier
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.badge.type = :badgeType AND ba.badge.tier = :badgeTier")
    List<BadgeAward> findByBadgeTypeAndTier(@Param("badgeType") String badgeType, @Param("badgeTier") String badgeTier);
    
    // Find awards for a specific user by badge type
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.user = :user AND ba.badge.type = :badgeType ORDER BY ba.awardedAt DESC")
    List<BadgeAward> findByUserAndBadgeTypeOrdered(@Param("user") User user, @Param("badgeType") String badgeType);
    
    // Find awards for a specific user by badge tier
    @Query("SELECT ba FROM BadgeAward ba WHERE ba.user = :user AND ba.badge.tier = :badgeTier ORDER BY ba.awardedAt DESC")
    List<BadgeAward> findByUserAndBadgeTierOrdered(@Param("user") User user, @Param("badgeTier") String badgeTier);
} 