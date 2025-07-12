package org.example.entity;

public enum ReactionType {
    LIKE,       // User likes the lead
    DISLIKE,    // User dislikes the lead
    VALIDATE,   // User validates the lead as useful
    INVALIDATE, // User marks the lead as invalid
    HELPFUL,    // User finds the lead helpful
    CONFUSING   // User finds the lead confusing
} 