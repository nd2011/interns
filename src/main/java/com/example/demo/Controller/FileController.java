package com.example.demo.Controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileController {

    // Kiểm tra quyền truy cập
    @GetMapping("/file")
    @Secured("ROLE_ADMIN")
    public String accessFile() {
        // Nếu người dùng là admin, cho phép truy cập vào file
        return "file";  // Tên view hiển thị file
    }

    // Trang lỗi khi người dùng không có quyền truy cập
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";  // Trả về trang lỗi 403.html
    }
}
