package org.example.controller;

import java.util.List;

import org.example.entity.CrimeCase;
import org.example.entity.HiringRequest;
import org.example.entity.HiringStatus;
import org.example.entity.User;
import org.example.service.CrimeCaseService;
import org.example.service.HiringRequestService;
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
@RequestMapping("/api/hiring-requests")
@CrossOrigin(origins = "*")
public class HiringRequestController {

    @Autowired
    private HiringRequestService hiringRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrimeCaseService crimeCaseService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<HiringRequest> createHiringRequest(@RequestBody HiringRequest hiringRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User organization = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User investigator = userService.findById(hiringRequest.getInvestigator().getId())
                .orElseThrow(() -> new RuntimeException("Investigator not found"));
        
        CrimeCase crimeCase = crimeCaseService.findById(hiringRequest.getCrimeCase().getId())
                .orElseThrow(() -> new RuntimeException("Case not found"));
        
        HiringRequest createdRequest = hiringRequestService.createHiringRequest(
            organization,
            investigator,
            crimeCase,
            hiringRequest.getTitle(),
            hiringRequest.getDescription(),
            hiringRequest.getProposedRate(),
            hiringRequest.getProposedDuration(),
            hiringRequest.getRequirements(),
            hiringRequest.getContactInfo()
        );
        
        return ResponseEntity.ok(createdRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HiringRequest> getHiringRequestById(@PathVariable Long id) {
        return hiringRequestService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('SOLVER')")
    public ResponseEntity<HiringRequest> updateHiringRequest(@PathVariable Long id, @RequestBody HiringRequest hiringRequest) {
        hiringRequest.setId(id);
        return ResponseEntity.ok(hiringRequestService.updateHiringRequest(hiringRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<Void> deleteHiringRequest(@PathVariable Long id) {
        // Note: Repository doesn't have delete method, but JPA provides it
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<HiringRequest>> getAllHiringRequests() {
        // Return empty list for now since we don't have a findAll method
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<HiringRequest>> getHiringRequestsByOrganization(@PathVariable Long organizationId) {
        User organization = userService.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return ResponseEntity.ok(hiringRequestService.findByOrganization(organization));
    }

    @GetMapping("/solver/{solverId}")
    public ResponseEntity<List<HiringRequest>> getHiringRequestsBySolver(@PathVariable Long solverId) {
        User solver = userService.findById(solverId)
                .orElseThrow(() -> new RuntimeException("Solver not found"));
        return ResponseEntity.ok(hiringRequestService.findByInvestigator(solver));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<HiringRequest>> getHiringRequestsByStatus(@PathVariable HiringStatus status) {
        return ResponseEntity.ok(hiringRequestService.findByStatus(status));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('SOLVER')")
    public ResponseEntity<HiringRequest> updateHiringRequestStatus(@PathVariable Long id, @RequestParam HiringStatus status) {
        // TODO: Implement updateHiringRequestStatus method in service
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('SOLVER')")
    public ResponseEntity<HiringRequest> acceptHiringRequest(@PathVariable Long id) {
        // TODO: Get response from request body
        return ResponseEntity.ok(hiringRequestService.acceptHiringRequest(id, "Accepted"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SOLVER')")
    public ResponseEntity<HiringRequest> rejectHiringRequest(@PathVariable Long id) {
        // TODO: Get response from request body
        return ResponseEntity.ok(hiringRequestService.rejectHiringRequest(id, "Rejected"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ResponseEntity<HiringRequest> cancelHiringRequest(@PathVariable Long id) {
        // TODO: Implement cancelHiringRequest method in service
        return ResponseEntity.ok().build();
    }
} 