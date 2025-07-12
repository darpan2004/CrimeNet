package org.example.repository;

import org.example.entity.CrimeCase;
import org.example.entity.RatingType;
import org.example.entity.User;
import org.example.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    
    // Basic find operations
    List<UserRating> findByRater(User rater);
    List<UserRating> findByRatedUser(User ratedUser);
    List<UserRating> findByCrimeCase(CrimeCase crimeCase);
    
    // Find specific rating
    Optional<UserRating> findByRaterAndRatedUserAndCrimeCase(User rater, User ratedUser, CrimeCase crimeCase);
    
    // Rating value queries
    List<UserRating> findByRating(Integer rating);
    List<UserRating> findByRatingGreaterThanEqual(Integer minRating);
    List<UserRating> findByRatingLessThanEqual(Integer maxRating);
    List<UserRating> findByRatingBetween(Integer minRating, Integer maxRating);
    
    // Type-based queries
    List<UserRating> findByType(RatingType type);
    List<UserRating> findByRatedUserAndType(User ratedUser, RatingType type);
    List<UserRating> findByRaterAndType(User rater, RatingType type);
    
    // Combined queries
    List<UserRating> findByRatedUserAndRatingGreaterThanEqual(User ratedUser, Integer minRating);
    List<UserRating> findByRaterAndRatingGreaterThanEqual(User rater, Integer minRating);
    List<UserRating> findByCrimeCaseAndRatingGreaterThanEqual(CrimeCase crimeCase, Integer minRating);
    
    // Date-based queries
    List<UserRating> findByRatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<UserRating> findByRatedAtGreaterThanEqual(LocalDateTime date);
    List<UserRating> findByRatedAtLessThanEqual(LocalDateTime date);
    
    // Find ratings for a specific case and user
    List<UserRating> findByCrimeCaseAndRatedUser(CrimeCase crimeCase, User ratedUser);
    List<UserRating> findByCrimeCaseAndRater(CrimeCase crimeCase, User rater);
    
    // Check if rating exists
    boolean existsByRaterAndRatedUserAndCrimeCase(User rater, User ratedUser, CrimeCase crimeCase);
    
    // Count ratings
    long countByRatedUser(User ratedUser);
    long countByRater(User rater);
    long countByCrimeCase(CrimeCase crimeCase);
    long countByRatedUserAndRatingGreaterThanEqual(User ratedUser, Integer minRating);
    
    // Calculate average rating for a user
    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.ratedUser = :ratedUser")
    Double calculateAverageRatingForUser(@Param("ratedUser") User ratedUser);
    
    // Calculate average rating for a user by type
    @Query("SELECT AVG(ur.rating) FROM UserRating ur WHERE ur.ratedUser = :ratedUser AND ur.type = :type")
    Double calculateAverageRatingForUserByType(@Param("ratedUser") User ratedUser, @Param("type") RatingType type);
    
    // Find recent ratings
    @Query("SELECT ur FROM UserRating ur ORDER BY ur.ratedAt DESC")
    List<UserRating> findRecentRatings();
    
    // Find top rated users
    @Query("SELECT ur.ratedUser, AVG(ur.rating) as avgRating FROM UserRating ur GROUP BY ur.ratedUser ORDER BY avgRating DESC")
    List<Object[]> findTopRatedUsers();
    
    // Find ratings with comments
    @Query("SELECT ur FROM UserRating ur WHERE ur.comment IS NOT NULL AND ur.comment != ''")
    List<UserRating> findRatingsWithComments();
    
    // Find ratings by organization for a solver
    @Query("SELECT ur FROM UserRating ur WHERE ur.rater.role = 'ORGANIZATION' AND ur.ratedUser = :solver")
    List<UserRating> findOrganizationRatingsForSolver(@Param("solver") User solver);
    
    // Find all ratings for a solver from a specific organization
    List<UserRating> findByRaterAndRatedUser(User rater, User ratedUser);
    
    // Find ratings for solved cases only
    @Query("SELECT ur FROM UserRating ur WHERE ur.crimeCase.status = 'SOLVED'")
    List<UserRating> findRatingsForSolvedCases();
    
    // Find ratings for a specific time period
    @Query("SELECT ur FROM UserRating ur WHERE ur.ratedAt BETWEEN :startDate AND :endDate")
    List<UserRating> findRatingsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 