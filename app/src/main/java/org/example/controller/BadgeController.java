package org.example.controller;

import java.util.List;

import org.example.entity.Badge;
import org.example.entity.BadgeAward;
import org.example.entity.BadgeTier;
import org.example.entity.BadgeType;
import org.example.entity.CrimeCase;
import org.example.entity.User;
import org.example.service.BadgeService;
import org.example.service.CrimeCaseService;
import org.example.service.UserService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/badges")
@CrossOrigin(origins = "*")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrimeCaseService crimeCaseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) {
        return ResponseEntity.ok(badgeService.createBadge(badge));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        return badgeService.findBadgeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        badge.setId(id);
        return ResponseEntity.ok(badgeService.updateBadge(badge));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(badgeService.findAllBadges());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Badge>> getBadgesByType(@PathVariable BadgeType type) {
        return ResponseEntity.ok(badgeService.findByType(type));
    }

    @GetMapping("/tier/{tier}")
    public ResponseEntity<List<Badge>> getBadgesByTier(@PathVariable BadgeTier tier) {
        return ResponseEntity.ok(badgeService.findByTier(tier));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BadgeAward>> getUserBadges(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(badgeService.findByUser(user));
    }

    @PostMapping("/award")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ResponseEntity<BadgeAward> awardBadge(@RequestBody BadgeAward badgeAward) {
        User user = userService.findById(badgeAward.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Badge badge = badgeService.findBadgeById(badgeAward.getBadge().getId())
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        
        CrimeCase crimeCase = null;
        if (badgeAward.getCrimeCase() != null) {
            crimeCase = crimeCaseService.findById(badgeAward.getCrimeCase().getId())
                    .orElse(null);
        }
        
        BadgeAward awardedBadge = badgeService.awardBadge(user, badge, crimeCase);
        return ResponseEntity.ok(awardedBadge);
    }

    @DeleteMapping("/award/{awardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revokeBadge(@PathVariable Long awardId) {
        // TODO: Implement revokeBadge method in service
        return ResponseEntity.ok().build();
    }

    @GetMapping("/awards")
    public ResponseEntity<List<BadgeAward>> getAllBadgeAwards() {
        // TODO: Implement getAllBadgeAwards method in service
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/awards/badge/{badgeId}")
    public ResponseEntity<List<BadgeAward>> getBadgeAwardsByBadge(@PathVariable Long badgeId) {
        Badge badge = badgeService.findBadgeById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        return ResponseEntity.ok(badgeService.findByBadge(badge));
    }

    @GetMapping("/awards/user/{userId}")
    public ResponseEntity<List<BadgeAward>> getBadgeAwardsByUser(@PathVariable Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(badgeService.findByUser(user));
    }
} 