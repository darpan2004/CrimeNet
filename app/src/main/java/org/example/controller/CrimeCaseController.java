package org.example.controller;

import java.util.List;

import org.example.entity.CaseDifficulty;
import org.example.entity.CaseStatus;
import org.example.entity.CaseType;
import org.example.entity.CrimeCase;
import org.example.entity.User;
import org.example.service.CrimeCaseService;
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
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CrimeCaseController {

    @Autowired
    private CrimeCaseService crimeCaseService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<CrimeCase> createCase(@RequestBody CrimeCase crimeCase) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CrimeCase createdCase = crimeCaseService.createCase(crimeCase, currentUser);
        return ResponseEntity.ok(createdCase);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrimeCase> getCaseById(@PathVariable Long id) {
        return crimeCaseService.findById(id)
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
        return ResponseEntity.ok(crimeCaseService.findAll());
    }

    @GetMapping("/public")
    public ResponseEntity<List<CrimeCase>> getPublicCases() {
        // TODO: Implement getPublicCases method in service - filter by privacy setting
        return ResponseEntity.ok(crimeCaseService.findAll());
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<CrimeCase>> getCasesByOrganization(@PathVariable Long organizationId) {
        User organization = userService.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return ResponseEntity.ok(crimeCaseService.findByPostedBy(organization));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CrimeCase>> getCasesByStatus(@PathVariable CaseStatus status) {
        return ResponseEntity.ok(crimeCaseService.findByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CrimeCase>> getCasesByType(@PathVariable CaseType type) {
        return ResponseEntity.ok(crimeCaseService.findByCaseType(type));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<CrimeCase>> getCasesByDifficulty(@PathVariable CaseDifficulty difficulty) {
        return ResponseEntity.ok(crimeCaseService.findByDifficulty(difficulty));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CrimeCase>> searchCases(@RequestParam String searchTerm) {
        return ResponseEntity.ok(crimeCaseService.searchByTitleOrDescription(searchTerm));
    }

    @PostMapping("/{id}/assign/{solverId}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<CrimeCase> assignSolver(@PathVariable Long id, @PathVariable Long solverId) {
        User solver = userService.findById(solverId)
                .orElseThrow(() -> new RuntimeException("Solver not found"));
        return ResponseEntity.ok(crimeCaseService.assignPrimarySolver(id, solver));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('ADMIN')")
    public ResponseEntity<CrimeCase> updateCaseStatus(@PathVariable Long id, @RequestParam CaseStatus status) {
        // TODO: Implement updateCaseStatus method in service
        // For now, we can use existing methods based on status
        switch (status) {
            case SOLVED:
                // Would need solution details from request body
                return ResponseEntity.ok().build();
            case CLOSED:
                return ResponseEntity.ok(crimeCaseService.closeCase(id));
            case OPEN:
                return ResponseEntity.ok(crimeCaseService.reopenCase(id));
            default:
                return ResponseEntity.ok().build();
        }
    }
}