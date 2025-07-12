package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leads")
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private String title;
    
    @Enumerated(EnumType.STRING)
    private LeadType type = LeadType.TEXT;
    
    @Enumerated(EnumType.STRING)
    private LeadStatus status = LeadStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private LeadVisibility visibility = LeadVisibility.PUBLIC;
    
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CrimeCase crimeCase;
    
    @ManyToOne
    @JoinColumn(name = "submitted_by")
    private User submittedBy;
    
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeadReaction> reactions = new ArrayList<>();
    
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LeadComment> comments = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "lead_tags", joinColumns = @JoinColumn(name = "lead_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    private String filePath;
    private String originalFileName;
    private Long fileSize;
    private String fileType;
    
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime validatedAt;
    
    @ManyToOne
    @JoinColumn(name = "validated_by")
    private User validatedBy;
    
    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public LeadType getType() { return type; }
    public void setType(LeadType type) { this.type = type; }
    
    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }
    
    public LeadVisibility getVisibility() { return visibility; }
    public void setVisibility(LeadVisibility visibility) { this.visibility = visibility; }
    
    public CrimeCase getCrimeCase() { return crimeCase; }
    public void setCrimeCase(CrimeCase crimeCase) { this.crimeCase = crimeCase; }
    
    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }
    
    public List<LeadReaction> getReactions() { return reactions; }
    public void setReactions(List<LeadReaction> reactions) { this.reactions = reactions; }
    
    public List<LeadComment> getComments() { return comments; }
    public void setComments(List<LeadComment> comments) { this.comments = comments; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    
    public User getValidatedBy() { return validatedBy; }
    public void setValidatedBy(User validatedBy) { this.validatedBy = validatedBy; }
} 