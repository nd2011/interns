package com.example.demo.Controller;

import com.example.demo.Entity.Product;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/product/products")
public class ProductsController {

    @Autowired
    private ProductRepository productRepository;

    // Define the directory where images will be saved
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product/products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("image") MultipartFile image) {
        if (!image.isEmpty()) {
            try {
                // Xác định đường dẫn tuyệt đối lưu ảnh
                String imagePath = "C:\\Users\\PC\\Downloads\\" + image.getOriginalFilename();  // Đường dẫn tuyệt đối

                // Tạo file từ đường dẫn tuyệt đối
                File file = new File(imagePath);  // Lưu ảnh vào thư mục "Downloads"
                image.transferTo(file);  // Lưu ảnh vào thư mục

                // Lưu đường dẫn tuyệt đối vào cơ sở dữ liệu
                product.setImage(imagePath);  // Set đường dẫn tuyệt đối vào sản phẩm

                // Lưu sản phẩm vào cơ sở dữ liệu
                productRepository.save(product);

                return "redirect:/product/products";  // Chuyển hướng về trang danh sách sản phẩm
            } catch (IOException e) {
                e.printStackTrace();
                return "error";  // Nếu có lỗi, trả về trang lỗi
            }
        }
        return "error";  // Nếu không có ảnh, trả về trang lỗi
    }


    // API: Lấy thông tin sản phẩm theo ID (trả về JSON)
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Giao diện sửa thông tin sản phẩm
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với id: " + id));
        model.addAttribute("product", product); // Thêm sản phẩm vào model để sửa
        return "product/edit-product"; // Hiển thị trang sửa trong thư mục product
    }

    // Xử lý khi submit form sửa sản phẩm
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute("product") Product updatedProduct) {
        updatedProduct.setId(id); // Đảm bảo ID không thay đổi khi cập nhật
        productRepository.save(updatedProduct); // Lưu sản phẩm đã cập nhật
        return "redirect:/product/products"; // Chuyển hướng về danh sách sản phẩm
    }

    // Xử lý xóa sản phẩm theo ID
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id); // Xóa sản phẩm nếu tồn tại
        }
        return "redirect:/product/products"; // Chuyển hướng về danh sách sau khi xóa
    }
}