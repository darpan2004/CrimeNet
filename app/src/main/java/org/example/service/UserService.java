package org.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Basic CRUD operations
    public User createUser(User user) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setEmailVerified(false);
        // For development: Auto-verify organizations, in production this should be manual
        user.setOrganizationVerified(user.getRole() == UserRole.ORGANIZATION);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Role-based operations
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> findOrganizations() {
        return userRepository.findByRole(UserRole.ORGANIZATION);
    }
    
    public List<User> findSolvers() {
        return userRepository.findByRole(UserRole.SOLVER);
    }
    
    public List<User> findVerifiedOrganizations() {
        return userRepository.findByRoleAndOrganizationVerified(UserRole.ORGANIZATION, true);
    }
    
    public List<User> findPendingVerificationOrganizations() {
        return userRepository.findByRoleAndOrganizationVerified(UserRole.ORGANIZATION, false);
    }
    
    // Organization verification
    public User verifyOrganization(Long organizationId) {
        Optional<User> userOpt = userRepository.findById(organizationId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == UserRole.ORGANIZATION) {
                user.setOrganizationVerified(true);
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        throw new IllegalArgumentException("User is not an organization or not found");
    }
    
    public User rejectOrganization(Long organizationId) {
        Optional<User> userOpt = userRepository.findById(organizationId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == UserRole.ORGANIZATION) {
                user.setOrganizationVerified(false);
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        throw new IllegalArgumentException("User is not an organization or not found");
    }
    
    // Solver operations
    public List<User> findAvailableForHireSolvers() {
        return userRepository.findByRoleAndAvailableForHire(UserRole.SOLVER, true);
    }
    
    public User setAvailableForHire(Long userId, boolean available, Double hourlyRate) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == UserRole.SOLVER) {
                user.setAvailableForHire(available);
                if (hourlyRate != null) {
                    user.setHourlyRate(hourlyRate);
                }
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        throw new IllegalArgumentException("User is not a solver or not found");
    }
    
    // Rating operations
    public void updateUserRating(User user) {
        // This will be called by RatingService to update user's average rating
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void incrementSolvedCases(User user) {
        user.setSolvedCasesCount(user.getSolvedCasesCount() + 1);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void updateActiveCasesCount(User user, int count) {
        user.setActiveCasesCount(count);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Badge operations
    public void addBadge(User user, String badge) {
        Set<String> badges = user.getBadges();
        badges.add(badge);
        user.setBadges(badges);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void removeBadge(User user, String badge) {
        Set<String> badges = user.getBadges();
        badges.remove(badge);
        user.setBadges(badges);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Expertise and interests
    public void addExpertiseArea(User user, String expertise) {
        Set<String> expertiseAreas = user.getExpertiseAreas();
        expertiseAreas.add(expertise);
        user.setExpertiseAreas(expertiseAreas);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void addInterest(User user, String interest) {
        Set<String> interests = user.getInterests();
        interests.add(interest);
        user.setInterests(interests);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Search operations
    public List<User> findSolversByExpertise(String expertise) {
        return userRepository.findByExpertiseArea(expertise);
    }
    
    public List<User> findSolversByInterest(String interest) {
        return userRepository.findByInterest(interest);
    }
    
    public List<User> findSolversByLocation(String location) {
        return userRepository.findByLocationContainingIgnoreCase(location);
    }
    
    public List<User> findTopSolvers() {
        return userRepository.findTopSolvers();
    }
    
    public List<User> findSolversByMinRating(Double minRating) {
        return userRepository.findByAverageRatingGreaterThanEqual(minRating);
    }
    
    public List<User> findSolversByMinCases(Integer minCases) {
        return userRepository.findBySolvedCasesCountGreaterThanEqual(minCases);
    }
    
    // Email verification
    public Optional<User> findByEmailVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token);
    }
    
    public User verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByEmailVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationExpiry(null);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Invalid email verification token");
    }
    
    // Password reset
    public Optional<User> findByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }
    
    public User setPasswordResetToken(User user, String token, LocalDateTime expiry) {
        user.setPasswordResetToken(token);
        user.setPasswordResetExpiry(expiry);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByPasswordResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPasswordResetExpiry() != null && 
                user.getPasswordResetExpiry().isAfter(LocalDateTime.now())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setPasswordResetToken(null);
                user.setPasswordResetExpiry(null);
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
        throw new IllegalArgumentException("Invalid or expired password reset token");
    }
    
    // Validation
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Last login update
    public void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }
} 