package org.example.entity;

public enum ParticipationRole {
    OWNER,      // Organization that posted the case
    LEADER,     // Team leader assigned to the case
    SOLVER,     // Active solver working on the case
    FOLLOWER    // User following the case but not actively solving
} 