package com.example.demo.Controller;

import com.example.demo.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthJwtController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Đăng nhập nhận JWT token
     * Endpoint: POST /api/auth/login
     * Body: { "username": "duong", "password": "123456" }
     * Trả về: { "token": "eyJhbGciOi..." }
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtUtil.generateToken(auth.getName());
        return Map.of("token", token);
    }

    /**
     * Test API cần xác thực bằng JWT
     * Gọi với Header: Authorization: Bearer <token>
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "✅ JWT hợp lệ! Xin chào từ server.");
    }
}
