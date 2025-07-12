package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_reactions")
public class LeadReaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    private ReactionType type;
    
    private LocalDateTime reactedAt;
    
    @PrePersist
    protected void onCreate() {
        reactedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }
    
    public LocalDateTime getReactedAt() { return reactedAt; }
    public void setReactedAt(LocalDateTime reactedAt) { this.reactedAt = reactedAt; }
} 