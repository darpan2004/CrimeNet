package org.example.controller;

public class AuthResponse {
    private String token;
    private String type;
    private Long id;
    private String username;
    private String email;
    private String role;

    public AuthResponse(String token, String type, Long id, String username, String email, String role) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
    public String getToken() { return token; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
} 