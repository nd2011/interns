package com.example.demo.Repository;

import com.example.demo.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Tìm sản phẩm theo tên (ví dụ)
    List<Product> findByName(String name);
}
