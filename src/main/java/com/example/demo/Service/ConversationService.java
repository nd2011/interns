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
        // Tìm cuộc trò chuyện giữa hai người dùng
        List<Conversation> conversations = conversationRepository.findByParticipantsContains(user1, user2);

        return conversations.stream()
                .filter(c -> c.getParticipants().size() == 2)
                .findFirst()
                .orElseGet(() -> {
                    // Tạo mới cuộc trò chuyện nếu không tìm thấy
                    Set<MyAppUser> participants = new HashSet<>(Set.of(user1, user2));
                    Conversation newConv = new Conversation();
                    newConv.setParticipants(participants);
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
