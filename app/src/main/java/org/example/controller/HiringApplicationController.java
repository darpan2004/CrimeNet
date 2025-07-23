package org.example.controller;

import org.example.dto.HiringApplicationRequestDTO;
import org.example.dto.HiringApplicationResponseDTO;
import org.example.entity.ApplicationStatus;
import org.example.entity.HiringApplication;
import org.example.entity.HiringPost;
import org.example.entity.User;
import org.example.service.HiringApplicationService;
import org.example.service.HiringPostService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/hiring-applications")
public class HiringApplicationController {
    @Autowired
    private HiringApplicationService hiringApplicationService;
    @Autowired
    private HiringPostService hiringPostService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<HiringApplicationResponseDTO> create(@RequestBody HiringApplicationRequestDTO dto) {
        HiringApplication application = fromRequestDTO(dto);
        application.setCreatedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);
        HiringApplication saved = hiringApplicationService.save(application);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HiringApplicationResponseDTO> getById(@PathVariable Long id) {
        Optional<HiringApplication> application = hiringApplicationService.findById(id);
        return application.map(a -> ResponseEntity.ok(toResponseDTO(a))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<HiringApplicationResponseDTO>> getByPost(@PathVariable Long postId) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        // Fetch the post
        HiringPost post = hiringPostService.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        // Only the recruiter who posted can see applications
        if (post.getRecruiter() == null || !post.getRecruiter().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        List<HiringApplication> applications = hiringApplicationService.findByPostId(postId);
        return ResponseEntity.ok(applications.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<List<HiringApplicationResponseDTO>> getByApplicant(@PathVariable Long applicantId) {
        List<HiringApplication> applications = hiringApplicationService.findByApplicantId(applicantId);
        return ResponseEntity.ok(applications.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<HiringApplicationResponseDTO>> getByStatus(@PathVariable ApplicationStatus status) {
        List<HiringApplication> applications = hiringApplicationService.findByStatus(status);
        return ResponseEntity.ok(applications.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hiringApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapping methods ---
    private HiringApplication fromRequestDTO(HiringApplicationRequestDTO dto) {
        HiringApplication application = new HiringApplication();
        HiringPost post = hiringPostService.findById(dto.postId).orElse(null);
        User applicant = userService.findById(dto.applicantId).orElse(null);
        application.setPost(post);
        application.setApplicant(applicant);
        application.setCoverLetter(dto.coverLetter);
        return application;
    }

    private HiringApplicationResponseDTO toResponseDTO(HiringApplication application) {
        HiringApplicationResponseDTO dto = new HiringApplicationResponseDTO();
        dto.id = application.getId();
        dto.postId = application.getPost() != null ? application.getPost().getId() : null;
        dto.applicantId = application.getApplicant() != null ? application.getApplicant().getId() : null;
        dto.coverLetter = application.getCoverLetter();
        dto.createdAt = application.getCreatedAt();
        dto.status = application.getStatus() != null ? application.getStatus().name() : null;
        // Add applicant details for frontend
        if (application.getApplicant() != null) {
            dto.applicantUsername = application.getApplicant().getUsername();
            dto.applicantEmail = application.getApplicant().getEmail();
            dto.applicantFirstName = application.getApplicant().getFirstName();
            dto.applicantLastName = application.getApplicant().getLastName();
            dto.applicantRole = application.getApplicant().getRole() != null ? application.getApplicant().getRole().name() : null;
        }
        return dto;
    }
} 