package org.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.entity.CaseComment;
import org.example.entity.DirectMessage;
import org.example.entity.User;
import org.example.repository.CaseCommentRepository;
import org.example.repository.DirectMessageRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Get DMs grouped by case and user for the current user
    public List<Map<String, Object>> getGroupedDMsByCase(User currentUser) {
        // 1. Find all cases where the user has commented
        List<CaseComment> userComments = caseCommentRepository.findByUser(currentUser);
        Set<Long> caseIds = userComments.stream().map(c -> c.getCrimeCase().getId()).collect(Collectors.toSet());
        if (caseIds.isEmpty()) return new ArrayList<>();
        // 2. For each case, find all users who have messaged the current user in that case
        List<CaseComment> allComments = caseCommentRepository.findByCrimeCaseIdIn(caseIds);
        Map<Long, String> caseIdToTitle = allComments.stream()
            .collect(Collectors.toMap(
                c -> c.getCrimeCase().getId(),
                c -> c.getCrimeCase().getTitle(),
                (a, b) -> a // keep first title
            ));
        Map<Long, Set<User>> caseIdToUsers = new HashMap<>();
        for (CaseComment comment : allComments) {
            Long caseId = comment.getCrimeCase().getId();
            if (!caseIdToUsers.containsKey(caseId)) caseIdToUsers.put(caseId, new HashSet<>());
            if (!comment.getUser().getId().equals(currentUser.getId())) {
                caseIdToUsers.get(caseId).add(comment.getUser());
            }
        }
        // 3. For each user in each case, get messages between currentUser and that user
        List<Map<String, Object>> result = new ArrayList<>();
        for (Long caseId : caseIdToUsers.keySet()) {
            Map<String, Object> caseMap = new HashMap<>();
            caseMap.put("caseId", caseId);
            caseMap.put("caseTitle", caseIdToTitle.get(caseId));
            List<Map<String, Object>> usersList = new ArrayList<>();
            for (User user : caseIdToUsers.get(caseId)) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getId());
                userMap.put("userName", user.getUsername());
                List<DirectMessage> messages = directMessageRepository.findChatBetweenUsers(currentUser, user);
                userMap.put("messages", messages);
                usersList.add(userMap);
            }
            caseMap.put("users", usersList);
            result.add(caseMap);
        }
        return result;
    }
} 