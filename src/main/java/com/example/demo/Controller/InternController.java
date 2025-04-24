package com.example.demo.Controller;

import com.example.demo.Entity.Intern;
import com.example.demo.Service.InternService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/") // Định nghĩa root URL, các @GetMapping phía dưới sẽ nối thêm vào
public class InternController {

    private final InternService service;

    // Constructor để Spring tự inject InternService (được đánh dấu @Service)
    public InternController(InternService service) {
        this.service = service;
    }

    // 🟡 Nếu người dùng vào "/intern", thì chuyển hướng đến "/interns"
    @GetMapping("/intern")
    public String redirectToInterns() {
        return "redirect:/interns";
    }

    // ✅ Trang index: hiển thị danh sách thực tập sinh
    @GetMapping
    public String index(Model model) {
        model.addAttribute("interns", service.findAll()); // Lấy danh sách từ DB
        return "index"; // Render file index.html trong templates
    }

    // ✅ Giao diện danh sách tất cả thực tập sinh
    @GetMapping("/interns")
    public String getAllInterns(Model model) {
        model.addAttribute("interns", service.findAll()); // Truyền list vào view
        return "interns"; // Render file interns.html
    }

    // ✅ Giao diện form thêm mới
    @GetMapping("/interns/add")
    public String showAddForm(Model model) {
        model.addAttribute("intern", new Intern()); // Truyền object rỗng để binding form
        return "add-intern"; // Render file add-intern.html
    }

    // ✅ Xử lý khi submit form thêm mới
    @PostMapping("/interns/add")
    public String addIntern(@ModelAttribute("intern") Intern intern) {
        service.save(intern); // Lưu vào DB
        return "redirect:/interns"; // Sau khi thêm xong, quay lại danh sách
    }

    // ✅ API REST: lấy 1 intern theo ID (trả về JSON)
    @ResponseBody
    @GetMapping("/interns/{id}")
    public ResponseEntity<Intern> getInternById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ API REST: tạo mới intern từ dữ liệu JSON (dùng với Postman hoặc FE gọi API)
    @ResponseBody
    @PostMapping("/interns")
    public Intern createIntern(@RequestBody Intern intern) {
        return service.save(intern);
    }

    // ✅ Giao diện sửa thực tập sinh
    @GetMapping("/interns/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Intern intern = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thực tập sinh với id: " + id));
        model.addAttribute("intern", intern); // Truyền intern đã có vào form để sửa
        return "edit-intern"; // Render form sửa
    }

    // ✅ Xử lý khi submit form sửa
    @PostMapping("/interns/edit/{id}")
    public String updateIntern(@PathVariable Integer id, @ModelAttribute("intern") Intern updatedIntern) {
        updatedIntern.setId(id); // Gán id lại để đảm bảo update đúng người
        service.save(updatedIntern); // Gọi lại save() sẽ update nếu ID đã tồn tại
        return "redirect:/interns"; // Quay lại danh sách
    }

    // ✅ Xử lý xóa thực tập sinh
    @PostMapping("/interns/delete/{id}")
    public String deleteIntern(@PathVariable Integer id) {
        if (service.findById(id).isPresent()) { // Kiểm tra có tồn tại không
            service.deleteById(id); // Xóa nếu có
        }
        return "redirect:/interns"; // Quay lại danh sách sau khi xóa
    }
}
