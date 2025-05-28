package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

import java.util.Set;

@RestController
@RequestMapping("/req")
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody MyAppUser user) {
        // Kiểm tra username hoặc email đã tồn tại chưa
        if (myAppUserRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Username is already taken."));
        }

        // Kiểm tra email đã tồn tại chưa
        if (myAppUserRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email is already registered."));
        }

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán role mặc định nếu chưa có
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of("ROLE_USER"));
        }

        // Gán ngày đăng ký hiện tại nếu chưa có
        if (user.getJoindate() == null || user.getJoindate().isEmpty()) {
            user.setJoindate(java.time.LocalDate.now().toString());
        }

        try {
            MyAppUser savedUser = myAppUserRepository.save(user);
            // Trả về user đã lưu (có thể loại bỏ mật khẩu trước khi trả về)
            savedUser.setPassword(null); // Ẩn mật khẩu khi trả về JSON

            // Trả về kết quả thành công
            return ResponseEntity.ok(new SuccessResponse("User registered successfully", savedUser));
        } catch (Exception e) {
            // Nếu có lỗi khi lưu người dùng vào DB
            return ResponseEntity.status(500).body(new ErrorResponse("An error occurred while saving the user."));
        }
    }

    // Phản hồi lỗi chung
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    // Phản hồi thành công chung
    public static class SuccessResponse {
        private String message;
        private MyAppUser user;

        public SuccessResponse(String message, MyAppUser user) {
            this.message = message;
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public MyAppUser getUser() {
            return user;
        }
    }
}
