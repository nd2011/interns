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
        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("User1 hoặc User2 bị null");
        }

        // 🔍 Tìm hội thoại giữa 2 người
        List<Conversation> conversations = conversationRepository.findByParticipantsContains(user1, user2);

        if (!conversations.isEmpty()) {
            return conversations.get(0);
        }

        // 🆕 Nếu chưa có, tạo mới
        Conversation newConv = new Conversation();
        Set<MyAppUser> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        newConv.setParticipants(participants);

        System.out.println("🔥 Tạo hội thoại mới giữa " + user1.getFullname() + " và " + user2.getFullname());
        return conversationRepository.save(newConv);
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

    public Conversation findConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }
}
