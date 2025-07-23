package org.example.repository;

import org.example.entity.HiringChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HiringChatMessageRepository extends JpaRepository<HiringChatMessage, Long> {
    // Custom query methods can be added here
    java.util.List<HiringChatMessage> findByApplicationIdOrderByTimestampAsc(Long applicationId);
} 