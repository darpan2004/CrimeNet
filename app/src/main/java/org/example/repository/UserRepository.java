package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.entity.User;
import org.example.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic find operations
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // Role-based queries
    List<User> findByRole(UserRole role);
    List<User> findByRoleAndOrganizationVerified(UserRole role, boolean verified);
    
    // Organization queries
    List<User> findByOrganizationType(String organizationType);
    List<User> findByOrganizationVerified(boolean verified);
    
    // Solver queries
    List<User> findByAvailableForHire(boolean availableForHire);
    List<User> findByRoleAndAvailableForHire(UserRole role, boolean availableForHire);
    
    // Rating-based queries
    List<User> findByAverageRatingGreaterThanEqual(Double minRating);
    List<User> findBySolvedCasesCountGreaterThanEqual(Integer minCases);
    
    // Location-based queries
    List<User> findByLocationContainingIgnoreCase(String location);
    
    // Expertise-based queries
    @Query("SELECT u FROM User u JOIN u.expertiseAreas e WHERE e = :expertise")
    List<User> findByExpertiseArea(@Param("expertise") String expertise);
    
    @Query("SELECT u FROM User u JOIN u.interests i WHERE i = :interest")
    List<User> findByInterest(@Param("interest") String interest);
    
    // Email verification
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String token);
    
    // Check if username or email exists
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Find top performers
    @Query("SELECT u FROM User u WHERE u.role = 'SOLVER' ORDER BY u.averageRating DESC, u.solvedCasesCount DESC")
    List<User> findTopSolvers();
    
    // Find organizations by verification status
    @Query("SELECT u FROM User u WHERE u.role = 'ORGANIZATION' AND u.organizationVerified = :verified")
    List<User> findOrganizationsByVerificationStatus(@Param("verified") boolean verified);
} 