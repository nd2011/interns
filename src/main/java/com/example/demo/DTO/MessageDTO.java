package com.example.demo.DTO;

import com.example.demo.Entity.Message;

import java.time.LocalDateTime;


public record MessageDTO(Long id, String content, String senderName, LocalDateTime timestamp) {
    public static MessageDTO from(Message m) {
        return new MessageDTO(
                m.getId(),
                m.getContent(),
                m.getSender().getFullname(),
                m.getTimestamp()
        );
    }
}