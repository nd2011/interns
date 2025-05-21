package com.example.demo.Service;

import java.util.List;
import java.util.Optional;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MyAppUserService implements UserDetailsService{

    @Autowired
    private MyAppUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyAppUser> user = repository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();

            // Chuyển roles (Set<String>) thành List<GrantedAuthority>
            List<SimpleGrantedAuthority> authorities = userObj.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .toList();

            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .authorities(authorities)  // Gán quyền cho user
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }


}