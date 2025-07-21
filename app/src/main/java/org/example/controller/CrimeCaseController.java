package org.example.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.dto.CommentDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<List<CaseSummary>> getAllCases() {
        List<CaseSummary> summaries = crimeCaseService.findAll().stream()
            .map(c -> new CaseSummary(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus() != null ? c.getStatus().toString() : null,
                c.getPostedAt() != null ? c.getPostedAt().toString() : null,
                c.getImageUrl(),
                c.getMediaUrl()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

    public static class CaseSummary {
        private Long id;
        private String title;
        private String description;
        private String status;
        private String postedAt;
        private String imageUrl;
        private String mediaUrl;

        public CaseSummary(Long id, String title, String description, String status, String postedAt, String imageUrl, String mediaUrl) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.status = status;
            this.postedAt = postedAt;
            this.imageUrl = imageUrl;
            this.mediaUrl = mediaUrl;
        }
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getPostedAt() { return postedAt; }
        public String getImageUrl() { return imageUrl; }
        public String getMediaUrl() { return mediaUrl; }
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

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getCaseComments(@PathVariable Long id) {
        return crimeCaseService.findById(id)
            .map(crimeCase -> {
                List<CommentDTO> comments = crimeCase.getComments().stream()
                    .map(c -> {
                        CommentDTO dto = new CommentDTO();
                        dto.setId(c.getId());
                        dto.setUserId(c.getUser() != null ? c.getUser().getId() : null);
                        dto.setAuthor(c.getUser() != null ? c.getUser().getUsername() : "Unknown");
                        dto.setContent(c.getContent());
                        dto.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
                        return dto;
                    })
                    .collect(Collectors.toList());
                return ResponseEntity.ok(comments);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addCaseComment(@PathVariable Long id, @RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Content is required"));
        }
        return crimeCaseService.findById(id)
            .map(crimeCase -> {
                User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                org.example.entity.CaseComment comment = new org.example.entity.CaseComment();
                comment.setContent(content);
                comment.setCrimeCase(crimeCase);
                comment.setUser(user);
                // createdAt is set by @PrePersist
                crimeCase.getComments().add(comment);
                crimeCaseService.updateCase(crimeCase);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}