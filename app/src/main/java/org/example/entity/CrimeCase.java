package org.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "crime_cases")
public class CrimeCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String location;
    @Enumerated(EnumType.STRING)
    private CaseType caseType;
    
    @Enumerated(EnumType.STRING)
    private CaseDifficulty difficulty;
    
    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    private CasePrivacy privacy = CasePrivacy.PUBLIC;
    
    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;
    
    // Case Solving Tracking
    @ManyToOne
    @JoinColumn(name = "primary_solver")
    private User primarySolver;
    
    @ManyToMany
    @JoinTable(
        name = "case_solvers",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "solver_id")
    )
    private List<User> assignedSolvers = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "solved_by")
    private User solvedBy;
    
    private String solution;
    private String solutionNotes;
    private LocalDateTime incidentDate;
    private LocalDateTime postedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime solvedAt;
    private LocalDateTime closedAt;
    
    // Badge and Rating System
    private boolean badgeAwarded = false;
    private String awardedBadge;
    private LocalDateTime badgeAwardedAt;
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaseFile> files = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaseComment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaseParticipation> participations = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evidence> evidences = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Suspect> suspects = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lead> leads = new ArrayList<>();
    
    @OneToMany(mappedBy = "crimeCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserRating> ratings = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "case_tags", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        postedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }
    
    public CaseDifficulty getDifficulty() { return difficulty; }
    public void setDifficulty(CaseDifficulty difficulty) { this.difficulty = difficulty; }
    
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    
    public CasePrivacy getPrivacy() { return privacy; }
    public void setPrivacy(CasePrivacy privacy) { this.privacy = privacy; }
    
    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }
    
    public User getPrimarySolver() { return primarySolver; }
    public void setPrimarySolver(User primarySolver) { this.primarySolver = primarySolver; }
    
    public List<User> getAssignedSolvers() { return assignedSolvers; }
    public void setAssignedSolvers(List<User> assignedSolvers) { this.assignedSolvers = assignedSolvers; }
    
    public User getSolvedBy() { return solvedBy; }
    public void setSolvedBy(User solvedBy) { this.solvedBy = solvedBy; }
    
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    
    public String getSolutionNotes() { return solutionNotes; }
    public void setSolutionNotes(String solutionNotes) { this.solutionNotes = solutionNotes; }
    
    public LocalDateTime getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }
    
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getSolvedAt() { return solvedAt; }
    public void setSolvedAt(LocalDateTime solvedAt) { this.solvedAt = solvedAt; }
    
    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
    
    public boolean isBadgeAwarded() { return badgeAwarded; }
    public void setBadgeAwarded(boolean badgeAwarded) { this.badgeAwarded = badgeAwarded; }
    
    public String getAwardedBadge() { return awardedBadge; }
    public void setAwardedBadge(String awardedBadge) { this.awardedBadge = awardedBadge; }
    
    public LocalDateTime getBadgeAwardedAt() { return badgeAwardedAt; }
    public void setBadgeAwardedAt(LocalDateTime badgeAwardedAt) { this.badgeAwardedAt = badgeAwardedAt; }
    
    public List<CaseFile> getFiles() { return files; }
    public void setFiles(List<CaseFile> files) { this.files = files; }
    
    public List<CaseComment> getComments() { return comments; }
    public void setComments(List<CaseComment> comments) { this.comments = comments; }
    
    public List<CaseParticipation> getParticipations() { return participations; }
    public void setParticipations(List<CaseParticipation> participations) { this.participations = participations; }
    
    public List<Evidence> getEvidences() { return evidences; }
    public void setEvidences(List<Evidence> evidences) { this.evidences = evidences; }
    
    public List<Suspect> getSuspects() { return suspects; }
    public void setSuspects(List<Suspect> suspects) { this.suspects = suspects; }
    
    public List<Lead> getLeads() { return leads; }
    public void setLeads(List<Lead> leads) { this.leads = leads; }
    
    public List<UserRating> getRatings() { return ratings; }
    public void setRatings(List<UserRating> ratings) { this.ratings = ratings; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}