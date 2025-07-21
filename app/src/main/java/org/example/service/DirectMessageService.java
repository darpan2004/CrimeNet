package org.example.service;

import org.example.entity.DirectMessage;
import org.example.entity.User;
import org.example.entity.CaseComment;
import org.example.repository.DirectMessageRepository;
import org.example.repository.UserRepository;
import org.example.repository.CaseCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectMessageService {
    @Autowired
    private DirectMessageRepository directMessageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaseCommentRepository caseCommentRepository;

    // Send a message
    public DirectMessage sendMessage(User sender, User receiver, String content) {
        DirectMessage message = new DirectMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        return directMessageRepository.save(message);
    }

    // Get chat history between two users
    public List<DirectMessage> getChat(User user1, User user2) {
        return directMessageRepository.findChatBetweenUsers(user1, user2);
    }

    // Get users eligible for DM (commented on same case)
    public Set<User> getEligibleDMUsers(User currentUser) {
        // Find all cases the user has commented on
        List<CaseComment> userComments = caseCommentRepository.findByUser(currentUser);
        Set<Long> caseIds = userComments.stream().map(c -> c.getCrimeCase().getId()).collect(Collectors.toSet());
        // Find all comments on those cases
        List<CaseComment> allComments = caseCommentRepository.findByCrimeCaseIdIn(caseIds);
        Set<User> eligible = allComments.stream().map(CaseComment::getUser).filter(u -> !u.getId().equals(currentUser.getId())).collect(Collectors.toSet());
        return eligible;
    }
} 