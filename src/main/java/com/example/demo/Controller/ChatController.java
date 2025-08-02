package com.example.demo.Controller;

import com.example.demo.DTO.ChatMessage;
import com.example.demo.Entity.Conversation;
import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Message;
import com.example.demo.Repository.MessageRepository;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private MessageRepository messageRepository;  // Đảm bảo có MessageRepository


    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName()).orElseThrow();
        MyAppUser receiver = myAppUserRepository.findById(chatMessage.getReceiverId()).orElseThrow();

        // Tìm hoặc tạo cuộc trò chuyện giữa người gửi và người nhận
        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);

        // Cập nhật thông tin người gửi và cuộc trò chuyện cho tin nhắn
        chatMessage.setSender(sender.getFullname());
        chatMessage.setConversationId(conversation.getId());

        // Lưu tin nhắn vào cơ sở dữ liệu
        Message message = new Message();
        message.setSender(sender);  // Người gửi
        message.setConversation(conversation);  // Cuộc trò chuyện
        message.setContent(chatMessage.getContent());  // Nội dung tin nhắn
        message.setTimestamp(LocalDateTime.now());  // Thời gian gửi tin nhắn
        messageRepository.save(message);
        //Truy vẫn tin nhắn
        List<Message> messages = messageRepository.findByConversationId(conversation.getId());

        // Gửi tin nhắn qua WebSocket (Gửi DTO chatMessage thay vì org.springframework.messaging.Message)
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
