package org.example.entity;

public enum LeadStatus {
    PENDING,    // Awaiting validation
    VALIDATED,  // Validated as useful
    REJECTED,   // Rejected as invalid
    INVESTIGATING // Under investigation
} 