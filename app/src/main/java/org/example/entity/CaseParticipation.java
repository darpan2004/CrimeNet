package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_participations")
public class CaseParticipation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CrimeCase crimeCase;
    
    @Enumerated(EnumType.STRING)
    private ParticipationRole role = ParticipationRole.FOLLOWER;
    
    @Enumerated(EnumType.STRING)
    private ParticipationStatus status = ParticipationStatus.ACTIVE;
    
    private LocalDateTime joinedAt;
    private LocalDateTime lastActivityAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        lastActivityAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastActivityAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public CrimeCase getCrimeCase() { return crimeCase; }
    public void setCrimeCase(CrimeCase crimeCase) { this.crimeCase = crimeCase; }
    
    public ParticipationRole getRole() { return role; }
    public void setRole(ParticipationRole role) { this.role = role; }
    
    public ParticipationStatus getStatus() { return status; }
    public void setStatus(ParticipationStatus status) { this.status = status; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
} 