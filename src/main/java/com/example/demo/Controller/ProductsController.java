package com.example.demo.Controller;

import com.example.demo.Entity.Product;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/product/products")
public class ProductsController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    // Define the directory where images will be saved
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @GetMapping
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 4; // Số sản phẩm mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productsPage = productService.findAll(pageable);
        model.addAttribute("products", productsPage);
        return "product/products";
    }
    @GetMapping("/show")
    public String showproductuser(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 6; // Số sản phẩm mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productsPage = productService.findAll(pageable);
        model.addAttribute("products", productsPage);
        return "product/product-list-user";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("intro") String intro) {
        product.setIntro(intro);
        productRepository.save(product);
        return "redirect:/product/products";
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
    public String updateProduct(@PathVariable Integer id,
                                @ModelAttribute("product") Product updatedProduct,
                                @RequestParam("intro") String intro) {
        updatedProduct.setId(id);
        updatedProduct.setIntro(intro);
        productRepository.save(updatedProduct);
        return "redirect:/product/products";
    }

    // Xử lý xóa sản phẩm theo ID
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id); // Xóa sản phẩm nếu tồn tại
        }
        return "redirect:/product/products"; // Chuyển hướng về danh sách sau khi xóa
    }
    @PostMapping(value = "/add-ajax", produces = "application/json")
    @ResponseBody
    public Map<String, Object> addProductAjax(@ModelAttribute Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product saved = productService.save(product); // hoặc repository.save(product)
            Map<String, Object> p = new HashMap<>();
            p.put("id", saved.getId());
            p.put("name", saved.getName());
            p.put("description", saved.getDescription());
            p.put("costPrice", saved.getCostPrice());
            p.put("price", saved.getPrice());
            p.put("quantity", saved.getQuantity());
            p.put("manufactureDate", saved.getManufactureDate());
            p.put("expiryDate", saved.getExpiryDate());
            p.put("intro", saved.getIntro());
            p.put("image", saved.getImage());
            result.put("success", true);
            result.put("product", p);
        } catch (Exception ex) {
            result.put("success", false);
            result.put("message", ex.getMessage());
        }
        return result;
    }

}