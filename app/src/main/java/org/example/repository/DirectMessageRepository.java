package org.example.repository;

import java.util.List;

import org.example.entity.DirectMessage;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    // Get all messages between two users (both directions)
    @Query("SELECT m FROM DirectMessage m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.sentAt ASC")
    List<DirectMessage> findChatBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    // Get the latest message for each chat (for DM list)
    @Query("SELECT m FROM DirectMessage m WHERE (m.sender = :user OR m.receiver = :user) AND m.sentAt = (SELECT MAX(m2.sentAt) FROM DirectMessage m2 WHERE (m2.sender = m.sender AND m2.receiver = m.receiver) OR (m2.sender = m.receiver AND m2.receiver = m.sender))")
    List<DirectMessage> findLatestMessagesForUser(@Param("user") User user);
} 