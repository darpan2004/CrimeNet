package org.example.controller;

import java.util.List;
import java.util.Set;

import org.example.entity.DirectMessage;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.DirectMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dm")
public class DirectMessageController {
    @Autowired
    private DirectMessageService directMessageService;
    @Autowired
    private UserRepository userRepository;

    // Send a message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody DMRequest request, Authentication authentication) {
        User sender = userRepository.findByUsername(authentication.getName()).orElse(null);
        User receiver = userRepository.findById(request.receiverId).orElse(null);
        if (sender == null || receiver == null) return ResponseEntity.badRequest().body("Invalid users");
        DirectMessage message = directMessageService.sendMessage(sender, receiver, request.content);
        return ResponseEntity.ok(message);
    }

    // Get chat history
    @GetMapping("/chat/{userId}")
    public ResponseEntity<?> getChat(@PathVariable Long userId, Authentication authentication) {
        User user1 = userRepository.findByUsername(authentication.getName()).orElse(null);
        User user2 = userRepository.findById(userId).orElse(null);
        if (user1 == null || user2 == null) return ResponseEntity.badRequest().body("Invalid users");
        List<DirectMessage> chat = directMessageService.getChat(user1, user2);
        return ResponseEntity.ok(chat);
    }

    // Get eligible DM users
    @GetMapping("/eligible")
    public ResponseEntity<?> getEligibleDMUsers(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body("Invalid user");
        Set<User> eligible = directMessageService.getEligibleDMUsers(user);
        return ResponseEntity.ok(eligible);
    }

    // DTO for send message
    public static class DMRequest {
        public Long receiverId;
        public String content;
    }
} 