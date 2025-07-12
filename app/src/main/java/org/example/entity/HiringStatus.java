package org.example.entity;

public enum HiringStatus {
    PENDING,    // Request sent, waiting for response
    ACCEPTED,   // Investigator accepted the request
    DECLINED,   // Investigator declined the request
    IN_PROGRESS, // Work has started
    COMPLETED,  // Work has been completed
    CANCELLED   // Request was cancelled
} 