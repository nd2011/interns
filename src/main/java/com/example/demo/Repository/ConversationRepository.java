package com.example.demo.Repository;

import com.example.demo.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // có thể thêm custom query sau nếu cần
}
