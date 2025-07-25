package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.entity.Rating;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    // Find all ratings for a specific user
    List<Rating> findByRatedUserOrderByCreatedAtDesc(User ratedUser);
    
    // Find all ratings given by a specific rater
    List<Rating> findByRaterOrderByCreatedAtDesc(User rater);
    
    // Check if a rater has already rated a specific user
    Optional<Rating> findByRaterAndRatedUser(User rater, User ratedUser);
    
    // Calculate average rating for a user
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.ratedUser = :user")
    Double calculateAverageRating(@Param("user") User user);
    
    // Count total ratings for a user
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratedUser = :user")
    Long countRatingsForUser(@Param("user") User user);
    
    // Find ratings by category for a user
    List<Rating> findByRatedUserAndCategoryOrderByCreatedAtDesc(User ratedUser, String category);
    
    // Find ratings for a specific case
    @Query("SELECT r FROM Rating r WHERE r.relatedCase.id = :caseId")
    List<Rating> findByCaseId(@Param("caseId") Long caseId);
    
    // Find recent ratings (last 30 days)
    @Query("SELECT r FROM Rating r WHERE r.ratedUser = :user AND r.createdAt >= CURRENT_DATE - 30")
    List<Rating> findRecentRatingsForUser(@Param("user") User user);
}
