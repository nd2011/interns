package com.example.demo.Service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.example.demo.Entity.Product;
import com.example.demo.Repository.ProductRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Product> findById(Integer id) {
        return repository.findById(id);
    }

    public Product save(Product product) {
        return repository.save(product);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
