package com.example.demo.Repository;

import com.example.demo.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // ĐÚNG: dùng _ để trỏ vào field id của quan hệ conversation
    List<Message> findByConversation_IdOrderByTimestampAsc(Long conversationId);

    // Nếu muốn mới nhất trước:
    // List<Message> findByConversation_IdOrderByTimestampDesc(Long conversationId);
}
