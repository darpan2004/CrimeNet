package org.example.service;

import org.example.entity.*;
import org.example.repository.UserRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRatingService {
    
    @Autowired
    private UserRatingRepository userRatingRepository;
    
    @Autowired
    private UserService userService;
    
    // Create rating
    public UserRating createRating(User rater, User ratedUser, CrimeCase crimeCase, 
                                 Integer rating, String comment, RatingType type) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        // Validate rater can rate this user
        if (!canUserRate(rater, ratedUser, crimeCase)) {
            throw new IllegalArgumentException("User cannot rate this solver");
        }
        
        // Check if rating already exists
        Optional<UserRating> existingRating = 
            userRatingRepository.findByRaterAndRatedUserAndCrimeCase(rater, ratedUser, crimeCase);
        
        if (existingRating.isPresent()) {
            throw new IllegalArgumentException("User has already rated this solver for this case");
        }
        
        // Create new rating
        UserRating userRating = new UserRating();
        userRating.setRater(rater);
        userRating.setRatedUser(ratedUser);
        userRating.setCrimeCase(crimeCase);
        userRating.setRating(rating);
        userRating.setComment(comment);
        userRating.setType(type);
        userRating.setRatedAt(LocalDateTime.now());
        
        UserRating savedRating = userRatingRepository.save(userRating);
        
        // Update user's average rating
        updateUserAverageRating(ratedUser);
        
        return savedRating;
    }
    
    // Update rating
    public UserRating updateRating(Long ratingId, Integer newRating, String newComment) {
        Optional<UserRating> ratingOpt = userRatingRepository.findById(ratingId);
        if (ratingOpt.isPresent()) {
            UserRating rating = ratingOpt.get();
            
            if (newRating != null) {
                if (newRating < 1 || newRating > 5) {
                    throw new IllegalArgumentException("Rating must be between 1 and 5");
                }
                rating.setRating(newRating);
            }
            
            if (newComment != null) {
                rating.setComment(newComment);
            }
            
            rating.setRatedAt(LocalDateTime.now());
            UserRating savedRating = userRatingRepository.save(rating);
            
            // Update user's average rating
            updateUserAverageRating(rating.getRatedUser());
            
            return savedRating;
        }
        throw new IllegalArgumentException("Rating not found");
    }
    
    // Delete rating
    public void deleteRating(Long ratingId) {
        Optional<UserRating> ratingOpt = userRatingRepository.findById(ratingId);
        if (ratingOpt.isPresent()) {
            UserRating rating = ratingOpt.get();
            User ratedUser = rating.getRatedUser();
            
            userRatingRepository.deleteById(ratingId);
            
            // Update user's average rating
            updateUserAverageRating(ratedUser);
        } else {
            throw new IllegalArgumentException("Rating not found");
        }
    }
    
    // Find operations
    public Optional<UserRating> findById(Long id) {
        return userRatingRepository.findById(id);
    }
    
    public List<UserRating> findByRatedUser(User ratedUser) {
        return userRatingRepository.findByRatedUser(ratedUser);
    }
    
    public List<UserRating> findByRater(User rater) {
        return userRatingRepository.findByRater(rater);
    }
    
    public List<UserRating> findByCrimeCase(CrimeCase crimeCase) {
        return userRatingRepository.findByCrimeCase(crimeCase);
    }
    
    public List<UserRating> findByRatedUserAndType(User ratedUser, RatingType type) {
        return userRatingRepository.findByRatedUserAndType(ratedUser, type);
    }
    
    public List<UserRating> findByRaterAndType(User rater, RatingType type) {
        return userRatingRepository.findByRaterAndType(rater, type);
    }
    
    public List<UserRating> findByRating(Integer rating) {
        return userRatingRepository.findByRating(rating);
    }
    
    public List<UserRating> findByRatingRange(Integer minRating, Integer maxRating) {
        return userRatingRepository.findByRatingBetween(minRating, maxRating);
    }
    
    // Get ratings for a specific case and user
    public List<UserRating> getRatingsForCaseAndUser(CrimeCase crimeCase, User ratedUser) {
        return userRatingRepository.findByCrimeCaseAndRatedUser(crimeCase, ratedUser);
    }
    
    public List<UserRating> getRatingsForCaseAndRater(CrimeCase crimeCase, User rater) {
        return userRatingRepository.findByCrimeCaseAndRater(crimeCase, rater);
    }
    
    // Recent ratings
    public List<UserRating> getRecentRatings() {
        return userRatingRepository.findRecentRatings();
    }
    
    public List<UserRating> getRatingsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return userRatingRepository.findRatingsInPeriod(startDate, endDate);
    }
    
    // Statistics
    public List<Object[]> getTopRatedUsers() {
        return userRatingRepository.findTopRatedUsers();
    }
    
    public long getRatingCount(User ratedUser) {
        return userRatingRepository.countByRatedUser(ratedUser);
    }
    
    public long getRatingCountByType(User ratedUser, RatingType type) {
        return userRatingRepository.findByRatedUserAndType(ratedUser, type).size();
    }
    
    public long getHighRatingCount(User ratedUser, Integer minRating) {
        return userRatingRepository.countByRatedUserAndRatingGreaterThanEqual(ratedUser, minRating);
    }
    
    // Average rating calculations
    public Double getAverageRating(User ratedUser) {
        return userRatingRepository.calculateAverageRatingForUser(ratedUser);
    }
    
    public Double getAverageRatingByType(User ratedUser, RatingType type) {
        return userRatingRepository.calculateAverageRatingForUserByType(ratedUser, type);
    }
    
    // Organization ratings for solver
    public List<UserRating> getOrganizationRatingsForSolver(User solver) {
        return userRatingRepository.findOrganizationRatingsForSolver(solver);
    }
    
    // Ratings with comments
    public List<UserRating> getRatingsWithComments() {
        return userRatingRepository.findRatingsWithComments();
    }
    
    // Check if rating exists
    public boolean hasRated(User rater, User ratedUser, CrimeCase crimeCase) {
        return userRatingRepository.existsByRaterAndRatedUserAndCrimeCase(rater, ratedUser, crimeCase);
    }
    
    // Get all ratings between two users
    public List<UserRating> getRatingsBetweenUsers(User rater, User ratedUser) {
        return userRatingRepository.findByRaterAndRatedUser(rater, ratedUser);
    }
    
    // Date-based queries
    public List<UserRating> getRatingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return userRatingRepository.findByRatedAtBetween(startDate, endDate);
    }
    
    public List<UserRating> getRatingsAfterDate(LocalDateTime date) {
        return userRatingRepository.findByRatedAtGreaterThanEqual(date);
    }
    
    public List<UserRating> getRatingsBeforeDate(LocalDateTime date) {
        return userRatingRepository.findByRatedAtLessThanEqual(date);
    }
    
    // Rating validation
    private boolean canUserRate(User rater, User ratedUser, CrimeCase crimeCase) {
        // Only organizations can rate solvers
        if (rater.getRole() != UserRole.ORGANIZATION) {
            return false;
        }
        
        // Can only rate solvers
        if (ratedUser.getRole() != UserRole.SOLVER) {
            return false;
        }
        
        // Can only rate for solved cases
        if (crimeCase.getStatus() != CaseStatus.SOLVED) {
            return false;
        }
        
        // Can only rate if the solver solved the case
        if (!ratedUser.equals(crimeCase.getSolvedBy())) {
            return false;
        }
        
        // Organization must be verified
        if (!rater.isOrganizationVerified()) {
            return false;
        }
        
        return true;
    }
    
    // Update user's average rating
    private void updateUserAverageRating(User ratedUser) {
        Double averageRating = getAverageRating(ratedUser);
        Integer totalRatings = (int) getRatingCount(ratedUser);
        
        if (averageRating != null) {
            ratedUser.setAverageRating(averageRating);
            ratedUser.setTotalRatings(totalRatings);
            userService.updateUserRating(ratedUser);
        }
    }
    
    // Get rating statistics for a user
    public RatingStatistics getRatingStatistics(User ratedUser) {
        Double overallAverage = getAverageRating(ratedUser);
        Double performanceAverage = getAverageRatingByType(ratedUser, RatingType.CASE_PERFORMANCE);
        Double communicationAverage = getAverageRatingByType(ratedUser, RatingType.COMMUNICATION);
        Double professionalismAverage = getAverageRatingByType(ratedUser, RatingType.PROFESSIONALISM);
        Double expertiseAverage = getAverageRatingByType(ratedUser, RatingType.EXPERTISE);
        Double reliabilityAverage = getAverageRatingByType(ratedUser, RatingType.RELIABILITY);
        
        long totalRatings = getRatingCount(ratedUser);
        long highRatings = getHighRatingCount(ratedUser, 4);
        long perfectRatings = getHighRatingCount(ratedUser, 5);
        
        return new RatingStatistics(
            overallAverage, performanceAverage, communicationAverage, 
            professionalismAverage, expertiseAverage, reliabilityAverage,
            totalRatings, highRatings, perfectRatings
        );
    }
    
    // Inner class for rating statistics
    public static class RatingStatistics {
        private final Double overallAverage;
        private final Double performanceAverage;
        private final Double communicationAverage;
        private final Double professionalismAverage;
        private final Double expertiseAverage;
        private final Double reliabilityAverage;
        private final long totalRatings;
        private final long highRatings;
        private final long perfectRatings;
        
        public RatingStatistics(Double overallAverage, Double performanceAverage, 
                              Double communicationAverage, Double professionalismAverage,
                              Double expertiseAverage, Double reliabilityAverage,
                              long totalRatings, long highRatings, long perfectRatings) {
            this.overallAverage = overallAverage;
            this.performanceAverage = performanceAverage;
            this.communicationAverage = communicationAverage;
            this.professionalismAverage = professionalismAverage;
            this.expertiseAverage = expertiseAverage;
            this.reliabilityAverage = reliabilityAverage;
            this.totalRatings = totalRatings;
            this.highRatings = highRatings;
            this.perfectRatings = perfectRatings;
        }
        
        // Getters
        public Double getOverallAverage() { return overallAverage; }
        public Double getPerformanceAverage() { return performanceAverage; }
        public Double getCommunicationAverage() { return communicationAverage; }
        public Double getProfessionalismAverage() { return professionalismAverage; }
        public Double getExpertiseAverage() { return expertiseAverage; }
        public Double getReliabilityAverage() { return reliabilityAverage; }
        public long getTotalRatings() { return totalRatings; }
        public long getHighRatings() { return highRatings; }
        public long getPerfectRatings() { return perfectRatings; }
        
        public double getHighRatingPercentage() {
            return totalRatings > 0 ? (double) highRatings / totalRatings * 100 : 0;
        }
        
        public double getPerfectRatingPercentage() {
            return totalRatings > 0 ? (double) perfectRatings / totalRatings * 100 : 0;
        }
    }
} 