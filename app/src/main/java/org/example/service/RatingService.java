package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.entity.CrimeCase;
import org.example.entity.Rating;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.repository.CrimeCaseRepository;
import org.example.repository.RatingRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CrimeCaseRepository caseRepository;
    
    /**
     * Rate a user - only RECRUITERS and ORGANIZATIONS can rate
     */
    public Rating rateUser(Long raterId, Long ratedUserId, Integer rating, 
                          String comment, String category, Long caseId) throws Exception {
        
        // Validate rater
        User rater = userRepository.findById(raterId)
            .orElseThrow(() -> new Exception("Rater not found"));
        
        // Check if rater has permission to rate
        if (rater.getRole() != UserRole.RECRUITER && rater.getRole() != UserRole.ORGANIZATION) {
            throw new Exception("Only recruiters and organizations can rate users");
        }
        
        // Validate rated user
        User ratedUser = userRepository.findById(ratedUserId)
            .orElseThrow(() -> new Exception("User to be rated not found"));
        
        // Prevent self-rating
        if (raterId.equals(ratedUserId)) {
            throw new Exception("Users cannot rate themselves");
        }
        
        // Check if already rated (optional - can allow re-rating)
        Optional<Rating> existingRating = ratingRepository.findByRaterAndRatedUser(rater, ratedUser);
        if (existingRating.isPresent()) {
            // Update existing rating instead of creating new one
            Rating existing = existingRating.get();
            existing.setRating(rating);
            existing.setComment(comment);
            existing.setCategory(category);
            
            if (caseId != null) {
                CrimeCase crimeCase = caseRepository.findById(caseId)
                    .orElseThrow(() -> new Exception("Case not found"));
                existing.setRelatedCase(crimeCase);
            }
            
            Rating savedRating = ratingRepository.save(existing);
            updateUserAverageRating(ratedUser);
            return savedRating;
        }
        
        // Create new rating
        Rating newRating = new Rating(rater, ratedUser, rating, comment, category);
        
        // Add case if provided
        if (caseId != null) {
            CrimeCase crimeCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new Exception("Case not found"));
            newRating.setRelatedCase(crimeCase);
        }
        
        Rating savedRating = ratingRepository.save(newRating);
        
        // Update user's average rating
        updateUserAverageRating(ratedUser);
        
        return savedRating;
    }
    
    /**
     * Get all ratings for a user
     */
    public List<Rating> getRatingsForUser(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
        return ratingRepository.findByRatedUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Get ratings given by a specific rater
     */
    public List<Rating> getRatingsByRater(Long raterId) throws Exception {
        User rater = userRepository.findById(raterId)
            .orElseThrow(() -> new Exception("Rater not found"));
        return ratingRepository.findByRaterOrderByCreatedAtDesc(rater);
    }
    
    /**
     * Get average rating for a user
     */
    public Double getAverageRating(Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
        Double average = ratingRepository.calculateAverageRating(user);
        return average != null ? average : 0.0;
    }
    
    /**
     * Update user's average rating and total ratings count
     */
    private void updateUserAverageRating(User user) {
        Double average = ratingRepository.calculateAverageRating(user);
        Long totalRatings = ratingRepository.countRatingsForUser(user);
        
        user.setAverageRating(average != null ? average : 0.0);
        user.setTotalRatings(totalRatings != null ? totalRatings.intValue() : 0);
        
        userRepository.save(user);
    }
    
    /**
     * Delete a rating (only by the original rater or admin)
     */
    public void deleteRating(Long ratingId, Long requesterId) throws Exception {
        Rating rating = ratingRepository.findById(ratingId)
            .orElseThrow(() -> new Exception("Rating not found"));
        
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new Exception("Requester not found"));
        
        // Only the original rater or admin can delete
        if (!rating.getRater().getId().equals(requesterId) && 
            requester.getRole() != UserRole.ORGANIZATION) {
            throw new Exception("Only the original rater or organization admin can delete this rating");
        }
        
        User ratedUser = rating.getRatedUser();
        ratingRepository.delete(rating);
        
        // Update user's average rating after deletion
        updateUserAverageRating(ratedUser);
    }
    
    /**
     * Check if a user can rate another user
     */
    public boolean canRate(Long raterId, Long ratedUserId) {
        try {
            User rater = userRepository.findById(raterId).orElse(null);
            if (rater == null) return false;
            
            // Only RECRUITERS and ORGANIZATIONS can rate
            if (rater.getRole() != UserRole.RECRUITER && rater.getRole() != UserRole.ORGANIZATION) {
                return false;
            }
            
            // Cannot rate themselves
            if (raterId.equals(ratedUserId)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
