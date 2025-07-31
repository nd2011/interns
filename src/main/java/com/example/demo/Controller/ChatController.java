package com.example.demo.Controller;

import com.example.demo.DTO.ChatMessage;
import com.example.demo.Entity.Conversation;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // ✅ đúng import
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private ConversationService conversationService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName()).orElseThrow();
        MyAppUser receiver = myAppUserRepository.findById(chatMessage.getReceiverId()).orElseThrow();

        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);

        chatMessage.setSender(sender.getFullname());
        chatMessage.setConversationId(conversation.getId());

        messagingTemplate.convertAndSend("/topic/conversation." + conversation.getId(), chatMessage);
    }

    @GetMapping("/chat")
    public String chatPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", myAppUserRepository.findAll()); // bạn có thể lọc chính mình nếu muốn
        return "Message/chat";
    }
    @GetMapping("/api/conversation/{receiverId}")
    @ResponseBody
    public Long getConversationId(@PathVariable Long receiverId, Principal principal) {
        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName()).orElseThrow();
        MyAppUser receiver = myAppUserRepository.findById(receiverId).orElseThrow();
        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);
        return conversation.getId();
    }
}
