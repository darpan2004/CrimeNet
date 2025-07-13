package org.example.controller;

import java.util.List;

import org.example.entity.Lead;
import org.example.entity.LeadStatus;
import org.example.entity.LeadType;
import org.example.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        // TODO: Extract parameters from request body and call createLead with proper parameters
        return ResponseEntity.ok().build();
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
        // TODO: Implement delete method in service
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        // TODO: Implement getAllLeads method in service
        return ResponseEntity.ok().build();
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Lead>> getLeadsByCase(@PathVariable Long caseId) {
        // TODO: Get CrimeCase by ID and call findByCrimeCase
        return ResponseEntity.ok().build();
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Lead>> getLeadsByCreator(@PathVariable Long creatorId) {
        // TODO: Get User by ID and call findBySubmittedBy
        return ResponseEntity.ok().build();
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
        // TODO: Implement upvote functionality
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/downvote")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Lead> downvoteLead(@PathVariable Long id) {
        // TODO: Implement downvote functionality
        return ResponseEntity.ok().build();
    }
} 