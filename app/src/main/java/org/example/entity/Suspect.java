package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suspects")
public class Suspect {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String alias;
    private String description;
    private String age;
    private String gender;
    private String location;
    private String occupation;
    
    @Enumerated(EnumType.STRING)
    private SuspectStatus status = SuspectStatus.UNKNOWN;
    
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CrimeCase crimeCase;
    
    @ManyToOne
    @JoinColumn(name = "added_by")
    private User addedBy;
    
    @ElementCollection
    @CollectionTable(name = "suspect_tags", joinColumns = @JoinColumn(name = "suspect_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    private LocalDateTime lastSeen;
    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
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
    
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public SuspectStatus getStatus() { return status; }
    public void setStatus(SuspectStatus status) { this.status = status; }
    
    public CrimeCase getCrimeCase() { return crimeCase; }
    public void setCrimeCase(CrimeCase crimeCase) { this.crimeCase = crimeCase; }
    
    public User getAddedBy() { return addedBy; }
    public void setAddedBy(User addedBy) { this.addedBy = addedBy; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 