package com.example.demo.Service;

import com.example.demo.Entity.Conversation;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.ConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Transactional
    public Conversation findOrCreatePrivateConversation(MyAppUser user1, MyAppUser user2) {
        Set<MyAppUser> pair = new HashSet<>(Set.of(user1, user2));

        List<Conversation> allConversations = conversationRepository.findAll(); // nếu cần eager fetch thì sửa lại ở repository

        return allConversations.stream()
                .filter(c -> c.getParticipants().size() == 2 && c.getParticipants().containsAll(pair))
                .findFirst()
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setParticipants(pair);
                    return conversationRepository.save(newConv);
                });
    }

    public List<Conversation> findAll() {
        return conversationRepository.findAll();
    }

    public Conversation save(Conversation c) {
        return conversationRepository.save(c);
    }

    public Conversation findById(Long id) {
        return conversationRepository.findById(id).orElse(null);
    }
}
