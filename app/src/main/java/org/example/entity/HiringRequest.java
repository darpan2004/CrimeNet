package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "hiring_requests")
public class HiringRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private HiringStatus status = HiringStatus.PENDING;
    
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private User organization;
    
    @ManyToOne
    @JoinColumn(name = "investigator_id")
    private User investigator;
    
    @ManyToOne
    @JoinColumn(name = "case_id")
    private CrimeCase crimeCase;
    
    private Double proposedRate;
    private String proposedDuration;
    private String requirements;
    private String contactInfo;
    
    @Column(columnDefinition = "TEXT")
    private String organizationMessage;
    
    @Column(columnDefinition = "TEXT")
    private String investigatorResponse;
    
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public HiringStatus getStatus() { return status; }
    public void setStatus(HiringStatus status) { this.status = status; }
    
    public User getOrganization() { return organization; }
    public void setOrganization(User organization) { this.organization = organization; }
    
    public User getInvestigator() { return investigator; }
    public void setInvestigator(User investigator) { this.investigator = investigator; }
    
    public CrimeCase getCrimeCase() { return crimeCase; }
    public void setCrimeCase(CrimeCase crimeCase) { this.crimeCase = crimeCase; }
    
    public Double getProposedRate() { return proposedRate; }
    public void setProposedRate(Double proposedRate) { this.proposedRate = proposedRate; }
    
    public String getProposedDuration() { return proposedDuration; }
    public void setProposedDuration(String proposedDuration) { this.proposedDuration = proposedDuration; }
    
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    
    public String getOrganizationMessage() { return organizationMessage; }
    public void setOrganizationMessage(String organizationMessage) { this.organizationMessage = organizationMessage; }
    
    public String getInvestigatorResponse() { return investigatorResponse; }
    public void setInvestigatorResponse(String investigatorResponse) { this.investigatorResponse = investigatorResponse; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
} 