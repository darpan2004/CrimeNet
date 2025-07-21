package org.example.controller;

import org.example.entity.DirectMessage;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.DirectMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DirectMessageWebSocketController {
    @Autowired
    private DirectMessageService directMessageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/dm.send")
    public void sendMessage(DMWebSocketMessage message) {
        User sender = userRepository.findById(message.senderId).orElse(null);
        User receiver = userRepository.findById(message.receiverId).orElse(null);
        if (sender == null || receiver == null) return;
        DirectMessage saved = directMessageService.sendMessage(sender, receiver, message.content);
        // Send to receiver's topic
        messagingTemplate.convertAndSend("/topic/dm." + receiver.getId(), saved);
        // Optionally, send to sender's topic as well
        messagingTemplate.convertAndSend("/topic/dm." + sender.getId(), saved);
    }

    public static class DMWebSocketMessage {
        public Long senderId;
        public Long receiverId;
        public String content;
    }
} 