package org.example.config;

import org.example.entity.*;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private CrimeCaseService crimeCaseService;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if no users exist
        if (userService.findAll().isEmpty()) {
            seedData();
        }
    }

    private void seedData() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@crimesolver.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);
        admin.setOrganizationVerified(true);
        admin.setAvailableForHire(false);
        admin.setHourlyRate(0.0);
        admin.setSolvedCasesCount(0);
        admin.setActiveCasesCount(0);
        admin.setAverageRating(0.0);
        admin.setTotalRatings(0);
        admin.setBadges(new HashSet<>());
        admin.setExpertiseAreas(new HashSet<>());
        admin.setInterests(new HashSet<>());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userService.createUser(admin);

        User organization = new User();
        organization.setUsername("police_dept");
        organization.setEmail("police@city.gov");
        organization.setPassword(passwordEncoder.encode("police123"));
        organization.setFirstName("City");
        organization.setLastName("Police Department");
        organization.setRole(UserRole.ORGANIZATION);
        organization.setEmailVerified(true);
        organization.setOrganizationVerified(true);
        organization.setOrganizationType("GOVERNMENT");
        organization.setAvailableForHire(false);
        organization.setHourlyRate(0.0);
        organization.setSolvedCasesCount(0);
        organization.setActiveCasesCount(0);
        organization.setAverageRating(0.0);
        organization.setTotalRatings(0);
        organization.setBadges(new HashSet<>());
        organization.setExpertiseAreas(new HashSet<>());
        organization.setInterests(new HashSet<>());
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        userService.createUser(organization);

        User solver = new User();
        solver.setUsername("detective_john");
        solver.setEmail("john@detective.com");
        solver.setPassword(passwordEncoder.encode("john123"));
        solver.setFirstName("John");
        solver.setLastName("Detective");
        solver.setRole(UserRole.SOLVER);
        solver.setEmailVerified(true);
        solver.setOrganizationVerified(false);
        solver.setAvailableForHire(true);
        solver.setHourlyRate(50.0);
        solver.setSolvedCasesCount(0);
        solver.setActiveCasesCount(0);
        solver.setAverageRating(0.0);
        solver.setTotalRatings(0);
        solver.setBadges(new HashSet<>());
        solver.setExpertiseAreas(new HashSet<>(Arrays.asList("Murder", "Theft", "Fraud")));
        solver.setInterests(new HashSet<>(Arrays.asList("Forensics", "Surveillance")));
        solver.setCreatedAt(LocalDateTime.now());
        solver.setUpdatedAt(LocalDateTime.now());
        userService.createUser(solver);

        // Create Test User for Postman Testing
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        String testPassword = "testpass123";
        String hashedTestPassword = passwordEncoder.encode(testPassword);
        System.out.println("DEBUG: Seeding testuser with password: '" + testPassword + "'");
        System.out.println("DEBUG: Hashed password: " + hashedTestPassword);
        testUser.setPassword(hashedTestPassword);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(UserRole.SOLVER);
        testUser.setEmailVerified(true);
        testUser.setOrganizationVerified(false);
        testUser.setAvailableForHire(true);
        testUser.setHourlyRate(45.0);
        testUser.setSolvedCasesCount(0);
        testUser.setActiveCasesCount(0);
        testUser.setAverageRating(0.0);
        testUser.setTotalRatings(0);
        testUser.setBadges(new HashSet<>());
        testUser.setExpertiseAreas(new HashSet<>(Arrays.asList("Theft", "Fraud")));
        testUser.setInterests(new HashSet<>(Arrays.asList("Digital Forensics", "Criminal Psychology")));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userService.createUser(testUser);

        // Create Badges
        Badge firstCaseBadge = new Badge();
        firstCaseBadge.setName("first_case");
        firstCaseBadge.setDisplayName("First Case");
        firstCaseBadge.setDescription("Solved your first case");
        firstCaseBadge.setType(BadgeType.ACHIEVEMENT);
        firstCaseBadge.setTier(BadgeTier.BRONZE);
        firstCaseBadge.setRequiredCases(1);
        firstCaseBadge.setIcon("🎯");
        firstCaseBadge.setCreatedAt(LocalDateTime.now());
        firstCaseBadge.setUpdatedAt(LocalDateTime.now());
        badgeService.createBadge(firstCaseBadge);

        Badge expertBadge = new Badge();
        expertBadge.setName("expert_solver");
        expertBadge.setDisplayName("Expert Solver");
        expertBadge.setDescription("Solved 10 cases");
        expertBadge.setType(BadgeType.ACHIEVEMENT);
        expertBadge.setTier(BadgeTier.SILVER);
        expertBadge.setRequiredCases(10);
        expertBadge.setIcon("🏆");
        expertBadge.setCreatedAt(LocalDateTime.now());
        expertBadge.setUpdatedAt(LocalDateTime.now());
        badgeService.createBadge(expertBadge);

        // Create Sample Case
        /*
        CrimeCase sampleCase = new CrimeCase();
        sampleCase.setTitle("Mysterious Theft at Central Bank");
        sampleCase.setDescription("A sophisticated theft occurred at Central Bank. Security cameras were disabled and the vault was accessed without triggering alarms.");
        sampleCase.setCaseType(CaseType.ROBBERY);
        sampleCase.setDifficulty(CaseDifficulty.MEDIUM);
        sampleCase.setPrivacy(CasePrivacy.PUBLIC);
        sampleCase.setStatus(CaseStatus.OPEN);
        sampleCase.setLocation("Central Bank, Downtown");
        sampleCase.setIncidentDate(LocalDateTime.now().minusDays(7));
        sampleCase.setPostedAt(LocalDateTime.now());
        sampleCase.setUpdatedAt(LocalDateTime.now());
        
        crimeCaseService.createCase(sampleCase, organization);
        */

        System.out.println("✅ Sample data seeded successfully!");
        System.out.println("👤 Admin: admin/admin123");
        System.out.println("🏢 Organization: police_dept/police123");
        System.out.println("🕵️ Solver: detective_john/john123");
        System.out.println("🧪 Test User: testuser/testpass123");
    }
} 