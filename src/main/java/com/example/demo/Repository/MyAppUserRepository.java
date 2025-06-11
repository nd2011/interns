package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyAppUserRepository extends JpaRepository<MyAppUser, Long> {

        Optional<MyAppUser> findByUsername(String username);

        boolean existsByUsername(String username);

        // Thêm phương thức kiểm tra tồn tại email
        boolean existsByEmail(String email);  // Không cần static, chỉ cần khai báo đúng như thế này
        Optional<MyAppUser> findById(Long id);

}
