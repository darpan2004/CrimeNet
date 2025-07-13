package org.example.controller;

import org.example.entity.CrimeCase;
import org.example.entity.CaseStatus;
import org.example.entity.CaseType;
import org.example.entity.CaseDifficulty;
import org.example.service.CrimeCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CrimeCaseController {

    @Autowired
    private CrimeCaseService crimeCaseService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<CrimeCase> createCase(@RequestBody CrimeCase crimeCase) {
        return ResponseEntity.ok(crimeCaseService.createCase(crimeCase));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrimeCase> getCaseById(@PathVariable Long id) {
        return crimeCaseService.getCaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<CrimeCase> updateCase(@PathVariable Long id, @RequestBody CrimeCase crimeCase) {
        crimeCase.setId(id);
        return ResponseEntity.ok(crimeCaseService.updateCase(crimeCase));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        crimeCaseService.deleteCase(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CrimeCase>> getAllCases() {
        return ResponseEntity.ok(crimeCaseService.getAllCases());
    }

    @GetMapping("/public")
    public ResponseEntity<List<CrimeCase>> getPublicCases() {
        return ResponseEntity.ok(crimeCaseService.getPublicCases());
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<CrimeCase>> getCasesByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(crimeCaseService.getCasesByOrganization(organizationId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CrimeCase>> getCasesByStatus(@PathVariable CaseStatus status) {
        return ResponseEntity.ok(crimeCaseService.getCasesByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CrimeCase>> getCasesByType(@PathVariable CaseType type) {
        return ResponseEntity.ok(crimeCaseService.getCasesByType(type));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<CrimeCase>> getCasesByDifficulty(@PathVariable CaseDifficulty difficulty) {
        return ResponseEntity.ok(crimeCaseService.getCasesByDifficulty(difficulty));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CrimeCase>> searchCases(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(crimeCaseService.searchCases(title, description, location));
    }

    @PostMapping("/{id}/assign/{solverId}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<CrimeCase> assignSolver(@PathVariable Long id, @PathVariable Long solverId) {
        return ResponseEntity.ok(crimeCaseService.assignSolver(id, solverId));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<CrimeCase> updateCaseStatus(@PathVariable Long id, @RequestParam CaseStatus status) {
        return ResponseEntity.ok(crimeCaseService.updateCaseStatus(id, status));
    }
}