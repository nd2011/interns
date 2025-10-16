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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        // username đang đăng nhập
        String loginUsername = principal.getName();
        model.addAttribute("username", loginUsername);

        // Lấy user hiện tại
        MyAppUser me = myAppUserRepository.findByUsername(loginUsername)
                .orElseThrow();

        // Lọc danh sách: bỏ chính mình
        List<MyAppUser> users = myAppUserRepository.findAll()
                .stream()
                .filter(u -> !u.getId().equals(me.getId()))
                .toList();

        // Truyền xuống view
        model.addAttribute("currentUserId", me.getId()); // dùng cho Thymeleaf/JS
        model.addAttribute("users", users);

        return "Message/chat";
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
        response.setSenderId(sender.getId());
        response.setSender(sender.getFullname());
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
    public ResponseEntity<?> getConversationId(@PathVariable Long receiverId, Principal principal) {
        if (principal == null) {
            // chưa đăng nhập
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn cần đăng nhập trước khi thực hiện hành động này.");
        }

        MyAppUser sender = myAppUserRepository.findByUsername(principal.getName()).orElseThrow();
        if (sender.getId().equals(receiverId)) {
            return ResponseEntity.badRequest().body("Không thể tạo hội thoại với chính mình");
        }

        MyAppUser receiver = myAppUserRepository.findById(receiverId).orElseThrow();
        Conversation conversation = conversationService.findOrCreatePrivateConversation(sender, receiver);

        // trả về ID hội thoại khi thành công
        return ResponseEntity.ok(conversation.getId());
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
