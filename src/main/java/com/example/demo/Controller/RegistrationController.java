package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/req")
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/signup", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody MyAppUser user) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra username đã tồn tại
        if (myAppUserRepository.existsByUsername(user.getUsername())) {
            response.put("success", false);
            response.put("message", "Username is already taken.");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra email đã tồn tại
        if (myAppUserRepository.existsByEmail(user.getEmail())) {
            response.put("success", false);
            response.put("message", "Email is already registered.");
            return ResponseEntity.badRequest().body(response);
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

            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while saving the user.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
