package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "badges")
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(unique = true)
    private String name;
    
    @NotBlank
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String icon;
    private String color;
    
    @Enumerated(EnumType.STRING)
    private BadgeType type;
    
    @Enumerated(EnumType.STRING)
    private BadgeTier tier = BadgeTier.BRONZE;
    
    private Integer requiredCases;
    private Integer requiredRating;
    private String requiredCaseType;
    private String requiredSpecialization;
    
    private boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BadgeAward> awards = new ArrayList<>();
    
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
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }
    
    public BadgeTier getTier() { return tier; }
    public void setTier(BadgeTier tier) { this.tier = tier; }
    
    public Integer getRequiredCases() { return requiredCases; }
    public void setRequiredCases(Integer requiredCases) { this.requiredCases = requiredCases; }
    
    public Integer getRequiredRating() { return requiredRating; }
    public void setRequiredRating(Integer requiredRating) { this.requiredRating = requiredRating; }
    
    public String getRequiredCaseType() { return requiredCaseType; }
    public void setRequiredCaseType(String requiredCaseType) { this.requiredCaseType = requiredCaseType; }
    
    public String getRequiredSpecialization() { return requiredSpecialization; }
    public void setRequiredSpecialization(String requiredSpecialization) { this.requiredSpecialization = requiredSpecialization; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<BadgeAward> getAwards() { return awards; }
    public void setAwards(List<BadgeAward> awards) { this.awards = awards; }
} 