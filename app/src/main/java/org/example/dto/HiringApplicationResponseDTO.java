package org.example.dto;

import java.time.LocalDateTime;

public class HiringApplicationResponseDTO {
    public Long id;
    public Long postId;
    public Long applicantId;
    public String coverLetter;
    public LocalDateTime createdAt;
    public String status;

    // Applicant details for recruiter view
    public String applicantUsername;
    public String applicantEmail;
    public String applicantFirstName;
    public String applicantLastName;
    public String applicantRole;
} 