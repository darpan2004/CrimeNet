package org.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HiringPostResponseDTO {
    public Long id;
    public Long recruiterId;
    public BigDecimal hourlyRate;
    public String caseType;
    public String overview;
    public String location;
    public LocalDateTime createdAt;
    public String status;
} 