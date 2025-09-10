package com.example.demo.DTO;

import com.example.demo.Entity.Message;
import java.time.LocalDateTime;

public record MessageDTO(
        Long id,
        Long senderId,        // 👈 thêm trường này
        String senderName,
        String content,
        LocalDateTime timestamp
) {
    public static MessageDTO from(Message m) {
        return new MessageDTO(
                m.getId(),
                m.getSender().getId(),        // 👈 map id người gửi
                m.getSender().getFullname(),
                m.getContent(),
                m.getTimestamp()
        );
    }
}
