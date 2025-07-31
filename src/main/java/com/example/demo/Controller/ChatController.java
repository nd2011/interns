package com.example.demo.Controller;

import com.example.demo.DTO.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // ✅ đúng import
import java.security.Principal;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setSender(principal.getName()); // ✅ đúng logic
        messagingTemplate.convertAndSend("/topic/conversation." + chatMessage.getConversationId(), chatMessage);
    }

    @GetMapping("/chat")
    public String chatPage(Model model) {
        return "Message/chat"; // trả về templates/Message/chat.html
    }
}
