package org.example.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String username;
    
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;
    
    @NotBlank
    private String password;
    
    private String firstName;
    private String lastName;
    private String bio;
    private String expertise;
    private String location;
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.SOLVER;
    
    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private Set<String> interests = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "user_expertise_areas", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "expertise_area")
    private Set<String> expertiseAreas = new HashSet<>();
    
    // Organization Verification (Simple)
    private boolean organizationVerified = false;
    private String organizationType; // "GOVERNMENT", "NGO", "PRIVATE"
    private String verificationDocument; // File path to verification document
    
    // Badge and Rating System
    @ElementCollection
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "badge")
    private Set<String> badges = new HashSet<>();
    
    private Double averageRating = 0.0;
    private Integer totalRatings = 0;
    private Integer solvedCasesCount = 0;
    private Integer activeCasesCount = 0;

    private boolean availableForHire = false;
    private Double hourlyRate;
    private String investigatorBio;
    private String experience;
    private String certifications;
    private String specializations;
    
    @ElementCollection
    @CollectionTable(name = "user_specializations", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "specialization")
    private Set<String> specializationsList = new HashSet<>();
    
    // Verification and Security
    private boolean emailVerified = false;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationExpiry;
    
    private String passwordResetToken;
    private LocalDateTime passwordResetExpiry;
    
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToMany
    @JoinTable(
        name = "user_connections",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "connected_user_id")
    )
    @JsonIgnore
    private Set<User> connections = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CaseParticipation> caseParticipations = new HashSet<>();
    
    @OneToMany(mappedBy = "ratedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRating> receivedRatings = new HashSet<>();
    
    @OneToMany(mappedBy = "rater", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRating> givenRatings = new HashSet<>();
    
    @OneToMany(mappedBy = "investigator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<HiringRequest> hiringRequests = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public Set<String> getInterests() { return interests; }
    public void setInterests(Set<String> interests) { this.interests = interests; }
    
    public Set<String> getExpertiseAreas() { return expertiseAreas; }
    public void setExpertiseAreas(Set<String> expertiseAreas) { this.expertiseAreas = expertiseAreas; }
    
    public boolean isOrganizationVerified() { return organizationVerified; }
    public void setOrganizationVerified(boolean organizationVerified) { this.organizationVerified = organizationVerified; }
    
    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
    
    public String getVerificationDocument() { return verificationDocument; }
    public void setVerificationDocument(String verificationDocument) { this.verificationDocument = verificationDocument; }
    
    public Set<String> getBadges() { return badges; }
    public void setBadges(Set<String> badges) { this.badges = badges; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }
    
    public Integer getSolvedCasesCount() { return solvedCasesCount; }
    public void setSolvedCasesCount(Integer solvedCasesCount) { this.solvedCasesCount = solvedCasesCount; }
    
    public Integer getActiveCasesCount() { return activeCasesCount; }
    public void setActiveCasesCount(Integer activeCasesCount) { this.activeCasesCount = activeCasesCount; }
    
    public boolean isAvailableForHire() { return availableForHire; }
    public void setAvailableForHire(boolean availableForHire) { this.availableForHire = availableForHire; }
    
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public String getInvestigatorBio() { return investigatorBio; }
    public void setInvestigatorBio(String investigatorBio) { this.investigatorBio = investigatorBio; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    
    public String getSpecializations() { return specializations; }
    public void setSpecializations(String specializations) { this.specializations = specializations; }
    
    public Set<String> getSpecializationsList() { return specializationsList; }
    public void setSpecializationsList(Set<String> specializationsList) { this.specializationsList = specializationsList; }
    
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public String getEmailVerificationToken() { return emailVerificationToken; }
    public void setEmailVerificationToken(String emailVerificationToken) { this.emailVerificationToken = emailVerificationToken; }
    
    public LocalDateTime getEmailVerificationExpiry() { return emailVerificationExpiry; }
    public void setEmailVerificationExpiry(LocalDateTime emailVerificationExpiry) { this.emailVerificationExpiry = emailVerificationExpiry; }
    
    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }
    
    public LocalDateTime getPasswordResetExpiry() { return passwordResetExpiry; }
    public void setPasswordResetExpiry(LocalDateTime passwordResetExpiry) { this.passwordResetExpiry = passwordResetExpiry; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<User> getConnections() { return connections; }
    public void setConnections(Set<User> connections) { this.connections = connections; }
    
    public Set<CaseParticipation> getCaseParticipations() { return caseParticipations; }
    public void setCaseParticipations(Set<CaseParticipation> caseParticipations) { this.caseParticipations = caseParticipations; }
    
    public Set<UserRating> getReceivedRatings() { return receivedRatings; }
    public void setReceivedRatings(Set<UserRating> receivedRatings) { this.receivedRatings = receivedRatings; }
    
    public Set<UserRating> getGivenRatings() { return givenRatings; }
    public void setGivenRatings(Set<UserRating> givenRatings) { this.givenRatings = givenRatings; }
    
    public Set<HiringRequest> getHiringRequests() { return hiringRequests; }
    public void setHiringRequests(Set<HiringRequest> hiringRequests) { this.hiringRequests = hiringRequests; }
}