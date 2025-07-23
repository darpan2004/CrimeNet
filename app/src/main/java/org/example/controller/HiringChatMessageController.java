package org.example.controller;

import org.example.dto.HiringChatMessageRequestDTO;
import org.example.dto.HiringChatMessageResponseDTO;
import org.example.entity.HiringApplication;
import org.example.entity.HiringChatMessage;
import org.example.entity.User;
import org.example.service.HiringApplicationService;
import org.example.service.HiringChatMessageService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hiring-chats")
public class HiringChatMessageController {
    @Autowired
    private HiringChatMessageService hiringChatMessageService;
    @Autowired
    private HiringApplicationService hiringApplicationService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<HiringChatMessageResponseDTO> sendMessage(@RequestBody HiringChatMessageRequestDTO dto) {
        HiringChatMessage message = fromRequestDTO(dto);
        message.setTimestamp(LocalDateTime.now());
        HiringChatMessage saved = hiringChatMessageService.save(message);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HiringChatMessageResponseDTO> getById(@PathVariable Long id) {
        Optional<HiringChatMessage> message = hiringChatMessageService.findById(id);
        return message.map(m -> ResponseEntity.ok(toResponseDTO(m))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<HiringChatMessageResponseDTO>> getByApplication(@PathVariable Long applicationId) {
        List<HiringChatMessage> messages = hiringChatMessageService.findByApplicationIdOrderByTimestampAsc(applicationId);
        return ResponseEntity.ok(messages.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hiringChatMessageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Mapping methods ---
    private HiringChatMessage fromRequestDTO(HiringChatMessageRequestDTO dto) {
        HiringChatMessage message = new HiringChatMessage();
        HiringApplication application = hiringApplicationService.findById(dto.applicationId).orElse(null);
        User sender = userService.findById(dto.senderId).orElse(null);
        message.setApplication(application);
        message.setSender(sender);
        message.setMessage(dto.message);
        return message;
    }

    private HiringChatMessageResponseDTO toResponseDTO(HiringChatMessage message) {
        HiringChatMessageResponseDTO dto = new HiringChatMessageResponseDTO();
        dto.id = message.getId();
        dto.applicationId = message.getApplication() != null ? message.getApplication().getId() : null;
        dto.senderId = message.getSender() != null ? message.getSender().getId() : null;
        dto.message = message.getMessage();
        dto.timestamp = message.getTimestamp();
        return dto;
    }
} 