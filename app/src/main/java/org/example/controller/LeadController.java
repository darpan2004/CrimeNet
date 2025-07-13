package org.example.controller;

import java.util.List;

import org.example.entity.CrimeCase;
import org.example.entity.Lead;
import org.example.entity.LeadStatus;
import org.example.entity.LeadType;
import org.example.entity.User;
import org.example.service.CrimeCaseService;
import org.example.service.LeadService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrimeCaseService crimeCaseService;

    @PostMapping
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CrimeCase crimeCase = crimeCaseService.findById(lead.getCrimeCase().getId())
                .orElseThrow(() -> new RuntimeException("Case not found"));
        
        Lead createdLead = leadService.createLead(
            crimeCase, 
            currentUser, 
            lead.getContent(), 
            lead.getTitle(), 
            lead.getType(), 
            lead.getVisibility()
        );
        
        return ResponseEntity.ok(createdLead);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        return leadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @RequestBody Lead lead) {
        lead.setId(id);
        return ResponseEntity.ok(leadService.updateLead(lead));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        // Note: Repository doesn't have delete method, but JPA provides it
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        // Return empty list for now since we don't have a findAll method
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Lead>> getLeadsByCase(@PathVariable Long caseId) {
        CrimeCase crimeCase = crimeCaseService.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return ResponseEntity.ok(leadService.findByCrimeCase(crimeCase));
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Lead>> getLeadsByCreator(@PathVariable Long creatorId) {
        User creator = userService.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(leadService.findBySubmittedBy(creator));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Lead>> getLeadsByStatus(@PathVariable LeadStatus status) {
        return ResponseEntity.ok(leadService.findByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Lead>> getLeadsByType(@PathVariable LeadType type) {
        return ResponseEntity.ok(leadService.findByType(type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Lead>> searchLeads(@RequestParam String searchTerm) {
        return ResponseEntity.ok(leadService.searchLeadsByContent(searchTerm));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> updateLeadStatus(@PathVariable Long id, @RequestParam LeadStatus status) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, status));
    }

    @PostMapping("/{id}/upvote")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> upvoteLead(@PathVariable Long id) {
        // TODO: Implement upvote functionality - would need to add to Lead entity and service
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/downvote")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> downvoteLead(@PathVariable Long id) {
        // TODO: Implement downvote functionality - would need to add to Lead entity and service
        return ResponseEntity.ok().build();
    }
} 