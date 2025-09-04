package com.example.demo.Controller;

import com.example.demo.DTO.ChatMessage;
import com.example.demo.DTO.MessageDTO;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private MessageRepository messageRepository;
    @GetMapping("/chat")
    public String chatPage(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("users", myAppUserRepository.findAll()); // bạn có thể lọc chính mình nếu muốn
        return "Message/chat";  // Trả về view "Message/chat"
    }

    // Phương thức nhận tin nhắn từ client và gửi lại qua WebSocket
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        // 1. Lấy thông tin người gửi & người nhận
        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName())
                .orElseThrow();
        MyAppUser receiver = myAppUserRepository.findById(chatMessage.getReceiverId())
                .orElseThrow();

        // 2. Tìm hoặc tạo cuộc trò chuyện
        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);

        // 3. Tạo entity Message để lưu DB
        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(chatMessage.getContent());   // nội dung
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        // 4. Tạo ChatMessage response để trả về WebSocket (chắc chắn có dữ liệu)
        ChatMessage response = new ChatMessage();
        response.setSender(sender.getFullname());          // tên đầy đủ
        response.setReceiverId(receiver.getId());          // id người nhận
        response.setContent(message.getContent());         // nội dung đã lưu
        response.setConversationId(conversation.getId());  // id hội thoại
        response.setTimestamp(message.getTimestamp());     // thời gian gửi

        // 5. Publish qua topic
        messagingTemplate.convertAndSend(
                "/topic/conversation." + conversation.getId(),
                response
        );
    }

    // API để lấy conversationId
    @GetMapping("/api/conversation/{receiverId}")
    @ResponseBody
    public Long getConversationId(@PathVariable Long receiverId, Principal principal) {
        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName()).orElseThrow();
        MyAppUser receiver = myAppUserRepository.findById(receiverId).orElseThrow();
        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);
        return conversation.getId();
    }
    // ChatController.java
    @GetMapping("/api/messages/{conversationId}")
    @ResponseBody
    public List<MessageDTO> getMessagesByConversationId(@PathVariable Long conversationId) {
        return messageRepository
                .findByConversation_IdOrderByTimestampAsc(conversationId)
                .stream()
                .map(MessageDTO::from)
                .toList();
    }



}
