package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.entity.HiringChatMessage;
import org.example.repository.HiringChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HiringChatMessageService {
    @Autowired
    private HiringChatMessageRepository hiringChatMessageRepository;

    public HiringChatMessage save(HiringChatMessage message) {
        return hiringChatMessageRepository.save(message);
    }

    public Optional<HiringChatMessage> findById(Long id) {
        return hiringChatMessageRepository.findById(id);
    }

    public List<HiringChatMessage> findByApplicationIdOrderByTimestampAsc(Long applicationId) {
        return hiringChatMessageRepository.findByApplicationIdOrderByTimestampAsc(applicationId);
    }

    public void deleteById(Long id) {
        hiringChatMessageRepository.deleteById(id);
    }
} 