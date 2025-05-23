package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

import java.util.Set;

@RestController
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/req/signup", consumes = "application/json")
    public MyAppUser createUser(@RequestBody MyAppUser user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán role mặc định
        user.setRoles(Set.of("ROLE_USER"));

        // Có thể set ngày đăng ký nếu muốn
        user.setJoindate(java.time.LocalDate.now().toString());

        return myAppUserRepository.save(user);
    }
}
