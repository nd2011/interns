package com.example.demo.Service;

import com.example.demo.Entity.Intern;
import com.example.demo.Repository.InternRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InternService {

    private final InternRepository repository;

    public InternService(InternRepository repository) {
        this.repository = repository;
    }

    public List<Intern> findAll() {
        return repository.findAll();
    }

    public Optional<Intern> findById(Integer id) {
        return repository.findById(id);
    }

    public Intern save(Intern intern) {
        return repository.save(intern);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
