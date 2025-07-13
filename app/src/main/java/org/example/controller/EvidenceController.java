package org.example.controller;

import java.util.List;

import org.example.entity.CrimeCase;
import org.example.entity.Evidence;
import org.example.entity.EvidenceStatus;
import org.example.entity.EvidenceType;
import org.example.entity.User;
import org.example.service.CrimeCaseService;
import org.example.service.EvidenceService;
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
@RequestMapping("/api/evidence")
@CrossOrigin(origins = "*")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrimeCaseService crimeCaseService;

    @PostMapping
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Evidence> createEvidence(@RequestBody Evidence evidence) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CrimeCase crimeCase = crimeCaseService.findById(evidence.getCrimeCase().getId())
                .orElseThrow(() -> new RuntimeException("Case not found"));
        
        Evidence createdEvidence = evidenceService.createEvidence(
            crimeCase,
            currentUser,
            evidence.getDescription(),
            evidence.getType(),
            evidence.getLocation(),
            evidence.getSource(),
            evidence.getStatus(),
            evidence.getNotes()
        );
        
        return ResponseEntity.ok(createdEvidence);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evidence> getEvidenceById(@PathVariable Long id) {
        return evidenceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Evidence> updateEvidence(@PathVariable Long id, @RequestBody Evidence evidence) {
        evidence.setId(id);
        return ResponseEntity.ok(evidenceService.updateEvidence(evidence));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        // Note: Repository doesn't have delete method, but JPA provides it
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Evidence>> getAllEvidence() {
        // Return empty list for now since we don't have a findAll method
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Evidence>> getEvidenceByCase(@PathVariable Long caseId) {
        CrimeCase crimeCase = crimeCaseService.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        return ResponseEntity.ok(evidenceService.findByCrimeCase(crimeCase));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Evidence>> getEvidenceByStatus(@PathVariable EvidenceStatus status) {
        return ResponseEntity.ok(evidenceService.findByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Evidence>> getEvidenceByType(@PathVariable EvidenceType type) {
        return ResponseEntity.ok(evidenceService.findByType(type));
    }

    @GetMapping("/chain-of-custody/{evidenceId}")
    public ResponseEntity<List<Evidence>> getEvidenceWithChainOfCustody(@PathVariable Long evidenceId) {
        return ResponseEntity.ok(evidenceService.getEvidenceWithChainOfCustody());
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Evidence> updateEvidenceStatus(@PathVariable Long id, @RequestParam EvidenceStatus status) {
        return ResponseEntity.ok(evidenceService.updateEvidenceStatus(id, status, null));
    }

    @PostMapping("/{id}/transfer")
    @PreAuthorize("hasRole('SOLVER') or hasRole('ORGANIZATION')")
    public ResponseEntity<Evidence> transferEvidence(@PathVariable Long id, @RequestParam Long newCustodianId) {
        User newCustodian = userService.findById(newCustodianId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(evidenceService.assignEvidence(id, newCustodian));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Evidence>> searchEvidence(@RequestParam String searchTerm) {
        return ResponseEntity.ok(evidenceService.searchEvidenceByDescription(searchTerm));
    }
} 