package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evidences")
public class Evidence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private EvidenceType type;
    
    @Enumerated(EnumType.STRING)
    private EvidenceStatus status = EvidenceStatus.COLLECTED;
    
    private String filePath;
    private String originalFileName;
    private Long fileSize;
    private String fileType;
    
    // Additional fields for evidence processing
    private String location;
    private String source;
    private String notes;
    
    @ManyToOne
    @JoinColumn(name = "collected_by")
    private User collectedBy;
    
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
    
    private LocalDateTime collectedAt;
    private LocalDateTime assignedAt;
    private LocalDateTime processingStartedAt;
    private LocalDateTime analyzedAt;
    
    @Column(columnDefinition = "TEXT")
    private String analysisResults;
    
    @Column(columnDefinition = "TEXT")
    private String conclusions;
    
    @Column(columnDefinition = "TEXT")
    private String chainOfCustody;
    
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CrimeCase crimeCase;
    
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    
    @ManyToMany
    @JoinTable(
        name = "evidence_suspects",
        joinColumns = @JoinColumn(name = "evidence_id"),
        inverseJoinColumns = @JoinColumn(name = "suspect_id")
    )
    private List<Suspect> linkedSuspects = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "evidence_tags", joinColumns = @JoinColumn(name = "evidence_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    private LocalDateTime incidentDate;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
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
    
    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }
    
    public EvidenceStatus getStatus() { return status; }
    public void setStatus(EvidenceStatus status) { this.status = status; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public CrimeCase getCrimeCase() { return crimeCase; }
    public void setCrimeCase(CrimeCase crimeCase) { this.crimeCase = crimeCase; }
    
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public List<Suspect> getLinkedSuspects() { return linkedSuspects; }
    public void setLinkedSuspects(List<Suspect> linkedSuspects) { this.linkedSuspects = linkedSuspects; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDateTime incidentDate) { this.incidentDate = incidentDate; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Additional getters and setters for new fields
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public User getCollectedBy() { return collectedBy; }
    public void setCollectedBy(User collectedBy) { this.collectedBy = collectedBy; }
    
    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    
    public LocalDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(LocalDateTime collectedAt) { this.collectedAt = collectedAt; }
    
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    
    public LocalDateTime getProcessingStartedAt() { return processingStartedAt; }
    public void setProcessingStartedAt(LocalDateTime processingStartedAt) { this.processingStartedAt = processingStartedAt; }
    
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalysisResults() { return analysisResults; }
    public void setAnalysisResults(String analysisResults) { this.analysisResults = analysisResults; }
    
    public String getConclusions() { return conclusions; }
    public void setConclusions(String conclusions) { this.conclusions = conclusions; }
    
    public String getChainOfCustody() { return chainOfCustody; }
    public void setChainOfCustody(String chainOfCustody) { this.chainOfCustody = chainOfCustody; }
} 