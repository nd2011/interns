package com.example.demo.Service;

import java.util.Optional;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private MyAppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyAppUser> user = repository.findByUsername(username);
        if (user.isPresent()) {
            return user.get(); // Trả về MyAppUser đã implement UserDetails
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
