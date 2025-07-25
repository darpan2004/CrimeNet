package org.example.controller;

import java.util.List;
import java.util.Map;

import org.example.entity.Rating;
import org.example.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {
    
    @Autowired
    private RatingService ratingService;
    
    /**
     * Rate a user
     */
    @PostMapping("/rate")
    public ResponseEntity<?> rateUser(@RequestBody Map<String, Object> ratingRequest) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Extract request data
            Long ratedUserId = Long.valueOf(ratingRequest.get("ratedUserId").toString());
            Integer rating = Integer.valueOf(ratingRequest.get("rating").toString());
            String comment = (String) ratingRequest.get("comment");
            String category = (String) ratingRequest.get("category");
            Long caseId = ratingRequest.get("caseId") != null ? 
                Long.valueOf(ratingRequest.get("caseId").toString()) : null;
            
            // Get rater ID (you'll need to implement getCurrentUserIdByUsername in UserService)
            // For now, assume you have this method or implement it
            // Long raterId = userService.findByUsername(username).get().getId();
            
            // Temporary: Get raterId from request (in production, get from authentication)
            Long raterId = Long.valueOf(ratingRequest.get("raterId").toString());
            
            Rating savedRating = ratingService.rateUser(raterId, ratedUserId, rating, comment, category, caseId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Rating submitted successfully",
                "rating", savedRating
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get ratings for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRatings(@PathVariable Long userId) {
        try {
            List<Rating> ratings = ratingService.getRatingsForUser(userId);
            Double averageRating = ratingService.getAverageRating(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "ratings", ratings,
                "averageRating", averageRating,
                "totalRatings", ratings.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get ratings given by a user
     */
    @GetMapping("/by-rater/{raterId}")
    public ResponseEntity<?> getRatingsByRater(@PathVariable Long raterId) {
        try {
            List<Rating> ratings = ratingService.getRatingsByRater(raterId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "ratings", ratings
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Check if a user can rate another user
     */
    @GetMapping("/can-rate/{raterId}/{ratedUserId}")
    public ResponseEntity<?> canRate(@PathVariable Long raterId, @PathVariable Long ratedUserId) {
        boolean canRate = ratingService.canRate(raterId, ratedUserId);
        
        return ResponseEntity.ok(Map.of(
            "canRate", canRate
        ));
    }
    
    /**
     * Delete a rating
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Get requester ID (you'll need to implement this)
            // Long requesterId = userService.findByUsername(username).get().getId();
            
            // Temporary: Get from request parameter
            Long requesterId = Long.valueOf("1"); // Replace with actual implementation
            
            ratingService.deleteRating(ratingId, requesterId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Rating deleted successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
