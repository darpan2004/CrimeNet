package org.example.entity;

public enum CaseStatus {
    OPEN,           // Case is open for solvers to join
    IN_PROGRESS,    // Case is being actively worked on
    SOLVED,         // Case has been solved
    CLOSED          // Case is closed (solved or abandoned)
} 