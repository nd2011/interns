package com.example.demo.Controller;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Service.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserManagementService service;

    public UserController(UserManagementService service) {
        this.service = service;
    }

    @GetMapping
    public String getAllUsers(Model model) {
        List<MyAppUser> allUsers = service.findAll();
        List<MyAppUser> nonAdminUsers = allUsers.stream()
                .filter(user -> !user.getRoles().contains("admin"))
                .collect(Collectors.toList());

        model.addAttribute("users", nonAdminUsers);
        return "/user/users";
    }


    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new MyAppUser());
        return "/user/add-user";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") MyAppUser user) {
        service.save(user);
        return "redirect:users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        MyAppUser user = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user với id: " + id));
        model.addAttribute("user", user);
        return "/user/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") MyAppUser updatedUser) {
        // Lấy user hiện tại từ DB
        Optional<MyAppUser> existingUser = service.findById(id);
        if (existingUser.isEmpty()) {
            // Xử lý nếu user không tồn tại (có thể redirect hoặc báo lỗi)
            return "redirect:/user/users?error=notfound";
        }
        // Giữ nguyên password cũ
        updatedUser.setPassword(existingUser.get().getPassword());
        updatedUser.setId(id);

        service.save(updatedUser);

        return "redirect:/users";
    }


    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
        }
        return "redirect:/user/users";
    }

    // API REST lấy user theo ID (trả về JSON)
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<MyAppUser> getUserById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
