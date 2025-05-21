package com.example.demo.Service;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {

    private final MyAppUserRepository repository;

    public UserManagementService(MyAppUserRepository repository) {
        this.repository = repository;
    }

    public List<MyAppUser> findAll() {
        return repository.findAll();
    }

    public Optional<MyAppUser> findById(Long id) {
        return repository.findById(id);
    }

    public MyAppUser save(MyAppUser user) {
        return repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
