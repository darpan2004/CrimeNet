package org.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.entity.Badge;
import org.example.entity.BadgeAward;
import org.example.entity.BadgeTier;
import org.example.entity.BadgeType;
import org.example.entity.CrimeCase;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.repository.BadgeAwardRepository;
import org.example.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BadgeService {
    
    @Autowired
    private BadgeRepository badgeRepository;
    
    @Autowired
    private BadgeAwardRepository badgeAwardRepository;
    
    @Autowired
    private UserService userService;
    
    // Badge management
    public Badge createBadge(Badge badge) {
        badge.setCreatedAt(LocalDateTime.now());
        badge.setUpdatedAt(LocalDateTime.now());
        return badgeRepository.save(badge);
    }
    
    public Badge updateBadge(Badge badge) {
        badge.setUpdatedAt(LocalDateTime.now());
        return badgeRepository.save(badge);
    }
    
    public Optional<Badge> findBadgeById(Long id) {
        return badgeRepository.findById(id);
    }
    
    public Optional<Badge> findBadgeByName(String name) {
        return badgeRepository.findByName(name);
    }
    
    public List<Badge> findAllBadges() {
        return badgeRepository.findAll();
    }
    
    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
    
    // Badge awarding
    public BadgeAward awardBadge(User user, Badge badge, CrimeCase crimeCase) {
        // Check if user already has this badge
        if (hasBadge(user, badge)) {
            throw new IllegalArgumentException("User already has this badge");
        }
        
        // Validate badge can be awarded
        if (!canAwardBadge(user, badge, crimeCase)) {
            throw new IllegalArgumentException("Badge cannot be awarded to this user");
        }
        
        // Create badge award
        BadgeAward badgeAward = new BadgeAward();
        badgeAward.setUser(user);
        badgeAward.setBadge(badge);
        badgeAward.setCrimeCase(crimeCase);
        badgeAward.setAwardedAt(LocalDateTime.now());
        
        BadgeAward savedAward = badgeAwardRepository.save(badgeAward);
        
        // Add badge to user's badge list
        userService.addBadge(user, badge.getName());
        
        return savedAward;
    }
    
    public BadgeAward awardBadgeByName(User user, String badgeName, CrimeCase crimeCase) {
        Optional<Badge> badgeOpt = findBadgeByName(badgeName);
        if (badgeOpt.isPresent()) {
            return awardBadge(user, badgeOpt.get(), crimeCase);
        }
        throw new IllegalArgumentException("Badge not found: " + badgeName);
    }
    
    // Remove badge
    public void removeBadge(User user, Badge badge) {
        Optional<BadgeAward> awardOpt = badgeAwardRepository.findByUserAndBadge(user, badge);
        if (awardOpt.isPresent()) {
            badgeAwardRepository.delete(awardOpt.get());
            userService.removeBadge(user, badge.getName());
        }
    }
    
    public void removeBadgeByName(User user, String badgeName) {
        Optional<Badge> badgeOpt = findBadgeByName(badgeName);
        if (badgeOpt.isPresent()) {
            removeBadge(user, badgeOpt.get());
        }
    }
    
    // Check badge status
    public boolean hasBadge(User user, Badge badge) {
        return badgeAwardRepository.existsByUserAndBadge(user, badge);
    }
    
    public boolean hasBadgeByName(User user, String badgeName) {
        Optional<Badge> badgeOpt = findBadgeByName(badgeName);
        if (badgeOpt.isPresent()) {
            return hasBadge(user, badgeOpt.get());
        }
        return false;
    }
    
    // Find operations
    public List<BadgeAward> findByUser(User user) {
        return badgeAwardRepository.findByUser(user);
    }
    
    public List<BadgeAward> findByBadge(Badge badge) {
        return badgeAwardRepository.findByBadge(badge);
    }
    
    public List<BadgeAward> findByCrimeCase(CrimeCase crimeCase) {
        return badgeAwardRepository.findByCrimeCase(crimeCase);
    }
    
    public List<BadgeAward> findByUserAndBadge(User user, Badge badge) {
        return badgeAwardRepository.findByUserAndBadgeOrdered(user, badge);
    }
    
    public Optional<BadgeAward> findAwardByUserAndBadge(User user, Badge badge) {
        return badgeAwardRepository.findByUserAndBadge(user, badge);
    }
    
    // Get user's badges
    public List<Badge> getUserBadges(User user) {
        return badgeRepository.findAllActiveBadges();
    }
    
    public List<Badge> getUserBadgesByType(User user, BadgeType type) {
        return badgeRepository.findByType(type);
    }
    
    public List<Badge> getUserBadgesByTier(User user, BadgeTier tier) {
        return badgeRepository.findByTier(tier);
    }
    
    // Badge search and filtering
    public List<Badge> findByType(BadgeType type) {
        return badgeRepository.findByType(type);
    }
    
    public List<Badge> findByTier(BadgeTier tier) {
        return badgeRepository.findByTier(tier);
    }
    
    public List<Badge> searchBadgesByName(String searchTerm) {
        return badgeRepository.findByName(searchTerm).stream().toList();
    }
    
    public List<Badge> findByCriteria(BadgeType type, BadgeTier tier) {
        return badgeRepository.findByTypeAndTier(type, tier);
    }
    
    // Statistics
    public long getBadgeCount(User user) {
        return badgeAwardRepository.countByUser(user);
    }
    
    public long getBadgeCountByType(User user, BadgeType type) {
        return badgeAwardRepository.countByUserAndBadgeType(user, type.name());
    }
    
    public long getBadgeCountByTier(User user, BadgeTier tier) {
        return badgeAwardRepository.countByUserAndBadgeTier(user, tier.name());
    }
    
    public List<Object[]> getMostAwardedBadges() {
        return badgeAwardRepository.findMostAwardedBadges();
    }
    
    public List<Object[]> getBadgeStatisticsByType() {
        return badgeRepository.countBadgesByType();
    }
    
    public List<Object[]> getBadgeStatisticsByTier() {
        return badgeRepository.countBadgesByTier();
    }
    
    // Recent awards
    public List<BadgeAward> getRecentAwards() {
        return badgeAwardRepository.findRecentAwards();
    }
    
    public List<BadgeAward> getAwardsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return badgeAwardRepository.findAwardsInPeriod(startDate, endDate);
    }
    
    // Auto-award badges based on achievements
    public void checkAndAwardAchievementBadges(User user) {
        // Check for case solving achievements
        checkCaseSolvingBadges(user);
        
        // Check for rating achievements
        checkRatingBadges(user);
        
        // Check for participation achievements
        checkParticipationBadges(user);
    }
    
    private void checkCaseSolvingBadges(User user) {
        int solvedCases = user.getSolvedCasesCount();
        
        // First case solved
        if (solvedCases == 1) {
            awardBadgeByNameIfNotHas(user, "First Case Solver", null);
        }
        
        // Milestone badges
        if (solvedCases >= 5) {
            awardBadgeByNameIfNotHas(user, "Case Solver", null);
        }
        if (solvedCases >= 10) {
            awardBadgeByNameIfNotHas(user, "Experienced Solver", null);
        }
        if (solvedCases >= 25) {
            awardBadgeByNameIfNotHas(user, "Veteran Solver", null);
        }
        if (solvedCases >= 50) {
            awardBadgeByNameIfNotHas(user, "Master Solver", null);
        }
        if (solvedCases >= 100) {
            awardBadgeByNameIfNotHas(user, "Legendary Solver", null);
        }
    }
    
    private void checkRatingBadges(User user) {
        Double averageRating = user.getAverageRating();
        Integer totalRatings = user.getTotalRatings();
        
        if (averageRating != null && totalRatings != null) {
            // High rating badges
            if (averageRating >= 4.5 && totalRatings >= 5) {
                awardBadgeByNameIfNotHas(user, "Highly Rated", null);
            }
            if (averageRating >= 4.8 && totalRatings >= 10) {
                awardBadgeByNameIfNotHas(user, "Excellence", null);
            }
            
            // Perfect rating badge
            if (averageRating == 5.0 && totalRatings >= 5) {
                awardBadgeByNameIfNotHas(user, "Perfect Score", null);
            }
        }
    }
    
    private void checkParticipationBadges(User user) {
        int activeCases = user.getActiveCasesCount();
        
        // Active participation badges
        if (activeCases >= 3) {
            awardBadgeByNameIfNotHas(user, "Active Participant", null);
        }
        if (activeCases >= 5) {
            awardBadgeByNameIfNotHas(user, "Dedicated Solver", null);
        }
    }
    
    private void awardBadgeByNameIfNotHas(User user, String badgeName, CrimeCase crimeCase) {
        if (!hasBadgeByName(user, badgeName)) {
            try {
                awardBadgeByName(user, badgeName, crimeCase);
            } catch (Exception e) {
                // Log error but don't fail the process
                System.err.println("Failed to award badge " + badgeName + " to user " + user.getUsername() + ": " + e.getMessage());
            }
        }
    }
    
    // Validation methods
    private boolean canAwardBadge(User user, Badge badge, CrimeCase crimeCase) {
        // Check if user is a solver
        if (user.getRole() != UserRole.SOLVER) {
            return false;
        }
        
        // Check badge requirements
        if (badge.getRequiredCases() != null && user.getSolvedCasesCount() < badge.getRequiredCases()) {
            return false;
        }
        
        if (badge.getRequiredRating() != null && 
            (user.getAverageRating() == null || user.getAverageRating() < badge.getRequiredRating())) {
            return false;
        }
        
        // Note: Badge entity doesn't have requiredRatings field
        // This would need to be added to the Badge entity if needed
        
        return true;
    }
    
    // Get available badges for user
    public List<Badge> getAvailableBadges(User user) {
        return badgeRepository.findMatchingBadges(user.getSolvedCasesCount(), user.getAverageRating(), null, null);
    }
    
    public List<Badge> getUnlockedBadges(User user) {
        return badgeRepository.findMatchingBadges(user.getSolvedCasesCount(), user.getAverageRating(), null, null);
    }
    
    public List<Badge> getLockedBadges(User user) {
        return badgeRepository.findAllActiveBadges();
    }
    
    // Badge progress tracking
    public BadgeProgress getBadgeProgress(User user, Badge badge) {
        int currentProgress = 0;
        int requiredProgress = 0;
        
        if (badge.getRequiredCases() != null) {
            currentProgress = user.getSolvedCasesCount();
            requiredProgress = badge.getRequiredCases();
        } else if (badge.getRequiredRating() != null) {
            currentProgress = user.getAverageRating() != null ? (int)(user.getAverageRating() * 10) : 0;
            requiredProgress = (int)(badge.getRequiredRating() * 10);
        }
        // Note: Badge entity doesn't have requiredRatings field
        
        double percentage = requiredProgress > 0 ? (double) currentProgress / requiredProgress * 100 : 0;
        
        return new BadgeProgress(badge, currentProgress, requiredProgress, percentage);
    }
    
    // Award badge manually (for RECRUITERS and ORGANIZATIONS)
    public BadgeAward awardBadgeManually(Long awarderId, Long userId, Long badgeId, 
                                       String reason, Long caseId) throws Exception {
        
        // Validate awarder
        User awarder = userService.findById(awarderId)
            .orElseThrow(() -> new Exception("Awarder not found"));
        
        // Check if awarder has permission to award badges
        if (awarder.getRole() != UserRole.RECRUITER && awarder.getRole() != UserRole.ORGANIZATION) {
            throw new Exception("Only recruiters and organizations can award badges manually");
        }
        
        // Validate user to receive badge
        User user = userService.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
        
        // Validate badge
        Badge badge = badgeRepository.findById(badgeId)
            .orElseThrow(() -> new Exception("Badge not found"));
        
        // Prevent self-awarding
        if (awarderId.equals(userId)) {
            throw new Exception("Users cannot award badges to themselves");
        }
        
        // Check if user already has this badge
        Optional<BadgeAward> existingAward = badgeAwardRepository.findByUserAndBadge(user, badge);
        if (existingAward.isPresent()) {
            throw new Exception("User already has this badge");
        }
        
        return awardBadge(user, badge, awarder, reason, caseId);
    }
    
    // Revoke a badge (only by the original awarder or admin)
    public void revokeBadge(Long badgeAwardId, Long requesterId) throws Exception {
        BadgeAward badgeAward = badgeAwardRepository.findById(badgeAwardId)
            .orElseThrow(() -> new Exception("Badge award not found"));
        
        User requester = userService.findById(requesterId)
            .orElseThrow(() -> new Exception("Requester not found"));
        
        // Only the original awarder or organization admin can revoke
        if (!badgeAward.getAwardedBy().getId().equals(requesterId) && 
            requester.getRole() != UserRole.ORGANIZATION) {
            throw new Exception("Only the original awarder or organization admin can revoke this badge");
        }
        
        badgeAwardRepository.delete(badgeAward);
        
        // Update user's badge count after revocation
        updateUserBadgesList(badgeAward.getUser());
    }
    
    // Check if a user can award badges
    public boolean canAwardBadges(Long userId) {
        try {
            User user = userService.findById(userId).orElse(null);
            if (user == null) return false;
            
            // Only RECRUITERS and ORGANIZATIONS can award badges manually
            return user.getRole() == UserRole.RECRUITER || user.getRole() == UserRole.ORGANIZATION;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Get badges awarded by a specific awarder
    public List<BadgeAward> getBadgesAwardedBy(Long awarderId) throws Exception {
        User awarder = userService.findById(awarderId)
            .orElseThrow(() -> new Exception("Awarder not found"));
        return badgeAwardRepository.findByAwardedByOrderByAwardedAtDesc(awarder);
    }
    
    // Update user's badges list after awarding/revoking
    private void updateUserBadgesList(User user) {
        List<BadgeAward> userBadges = badgeAwardRepository.findByUser(user);
        
        // Update the badges list in the user entity
        user.getBadges().clear();
        for (BadgeAward award : userBadges) {
            user.getBadges().add(award.getBadge().getName());
        }
        
        userService.updateUser(user);
    }
    
    // Modified awardBadge method to accept awarder parameter
    public BadgeAward awardBadge(User user, Badge badge, User awardedBy, String reason, Long caseId) {
        BadgeAward award = new BadgeAward();
        award.setUser(user);
        award.setBadge(badge);
        award.setAwardedBy(awardedBy);
        award.setReason(reason);
        
        // Add case if provided
        if (caseId != null) {
            // Note: You might need to inject CrimeCaseRepository or CaseService here
            // award.setCrimeCase(crimeCase);
        }
        
        BadgeAward savedAward = badgeAwardRepository.save(award);
        
        // Update user badges
        updateUserBadgesList(user);
        
        return savedAward;
    }
    
    // Inner class for badge progress
    public static class BadgeProgress {
        private final Badge badge;
        private final int currentProgress;
        private final int requiredProgress;
        private final double percentage;
        
        public BadgeProgress(Badge badge, int currentProgress, int requiredProgress, double percentage) {
            this.badge = badge;
            this.currentProgress = currentProgress;
            this.requiredProgress = requiredProgress;
            this.percentage = percentage;
        }
        
        // Getters
        public Badge getBadge() { return badge; }
        public int getCurrentProgress() { return currentProgress; }
        public int getRequiredProgress() { return requiredProgress; }
        public double getPercentage() { return percentage; }
        public boolean isCompleted() { return percentage >= 100; }
    }
} 