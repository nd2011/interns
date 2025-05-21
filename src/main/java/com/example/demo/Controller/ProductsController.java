package com.example.demo.Controller;

import com.example.demo.Entity.Product;
import com.example.demo.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/product/products") // Định nghĩa root URL cho các sản phẩm
public class ProductsController {

    private final ProductService service;

    // Constructor để Spring tự inject ProductService
    public ProductsController(ProductService service) {
        this.service = service;
    }

    // Trang hiển thị danh sách sản phẩm
    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", service.findAll()); // Thêm danh sách sản phẩm vào model
        return "product/products"; // Render trang danh sách sản phẩm trong thư mục product
    }

    // Giao diện form thêm sản phẩm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product()); // Tạo object rỗng để binding form
        return "product/add-product"; // Hiển thị trang thêm sản phẩm trong thư mục product
    }

    // Xử lý khi submit form thêm sản phẩm mới
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product) {
        product.setCreatedAt(LocalDateTime.now());
        service.save(product); // Lưu sản phẩm vào cơ sở dữ liệu
        return "redirect:/product/products"; // Chuyển hướng về danh sách sản phẩm
    }

    // API: Lấy thông tin sản phẩm theo ID (trả về JSON)
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Giao diện sửa thông tin sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id));
        model.addAttribute("product", product); // Thêm sản phẩm vào model để sửa
        return "product/edit-product"; // Hiển thị trang sửa trong thư mục product
    }

    // Xử lý khi submit form sửa sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute("product") Product updatedProduct) {
        updatedProduct.setId(id); // Đảm bảo ID không thay đổi khi cập nhật
        service.save(updatedProduct); // Lưu sản phẩm đã cập nhật
        return "redirect:/product/products"; // Chuyển hướng về danh sách sản phẩm
    }

    // Xử lý xóa sản phẩm theo ID
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id); // Xóa sản phẩm nếu tồn tại
        }
        return "redirect:/product/products"; // Chuyển hướng về danh sách sau khi xóa
    }
}