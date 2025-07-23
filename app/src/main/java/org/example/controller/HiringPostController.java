package org.example.controller;

import org.example.dto.HiringPostRequestDTO;
import org.example.dto.HiringPostResponseDTO;
import org.example.entity.HiringPost;
import org.example.entity.HiringPostStatus;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.service.HiringPostService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hiring-posts")
public class HiringPostController {
    @Autowired
    private HiringPostService hiringPostService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<HiringPostResponseDTO> create(@RequestBody HiringPostRequestDTO dto) {
        User recruiter = userService.findById(dto.recruiterId).orElse(null);
        if (recruiter == null || recruiter.getRole() != UserRole.RECRUITER) {
            return ResponseEntity.status(403).build();
        }
        HiringPost post = fromRequestDTO(dto);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(HiringPostStatus.OPEN);
        HiringPost saved = hiringPostService.save(post);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<HiringPostResponseDTO>> getAll() {
        List<HiringPost> posts = hiringPostService.findAll();
        return ResponseEntity.ok(posts.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HiringPostResponseDTO> getById(@PathVariable Long id) {
        Optional<HiringPost> post = hiringPostService.findById(id);
        return post.map(p -> ResponseEntity.ok(toResponseDTO(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<HiringPostResponseDTO>> getByRecruiter(@PathVariable Long recruiterId) {
        List<HiringPost> posts = hiringPostService.findByRecruiterId(recruiterId);
        return ResponseEntity.ok(posts.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<HiringPostResponseDTO>> getByStatus(@PathVariable HiringPostStatus status) {
        List<HiringPost> posts = hiringPostService.findByStatus(status);
        return ResponseEntity.ok(posts.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/caseType/{caseType}")
    public ResponseEntity<List<HiringPostResponseDTO>> getByCaseType(@PathVariable String caseType) {
        List<HiringPost> posts = hiringPostService.findByCaseType(caseType);
        return ResponseEntity.ok(posts.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<HiringPostResponseDTO>> getByLocation(@PathVariable String location) {
        List<HiringPost> posts = hiringPostService.findByLocation(location);
        return ResponseEntity.ok(posts.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hiringPostService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapping methods ---
    private HiringPost fromRequestDTO(HiringPostRequestDTO dto) {
        HiringPost post = new HiringPost();
        User recruiter = userService.findById(dto.recruiterId).orElse(null);
        post.setRecruiter(recruiter);
        post.setHourlyRate(dto.hourlyRate);
        post.setCaseType(dto.caseType);
        post.setOverview(dto.overview);
        post.setLocation(dto.location);
        return post;
    }

    private HiringPostResponseDTO toResponseDTO(HiringPost post) {
        HiringPostResponseDTO dto = new HiringPostResponseDTO();
        dto.id = post.getId();
        dto.recruiterId = post.getRecruiter() != null ? post.getRecruiter().getId() : null;
        dto.hourlyRate = post.getHourlyRate();
        dto.caseType = post.getCaseType();
        dto.overview = post.getOverview();
        dto.location = post.getLocation();
        dto.createdAt = post.getCreatedAt();
        dto.status = post.getStatus() != null ? post.getStatus().name() : null;
        return dto;
    }
} 