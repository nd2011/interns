// Khai báo package và import các thư viện cần thiết
package com.example.demo.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.Service.MyAppUserService;

import lombok.AllArgsConstructor;

@Configuration // Đánh dấu lớp này là một lớp cấu hình Spring
@EnableWebSecurity // Kích hoạt Spring Security cho ứng dụng
@AllArgsConstructor // Tự động sinh constructor với tất cả các trường final
public class SecurityConfig {


    private final MyAppUserService appUserService;




    // Bean cung cấp UserDetailsService để Spring dùng để load user từ DB
    @Bean
    public UserDetailsService userDetailsService(){
        return appUserService; // appUserService implements UserDetailsService
    }

    // Bean cấu hình AuthenticationProvider sử dụng DAO và mã hóa mật khẩu
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService); // Lấy user từ DB
        provider.setPasswordEncoder(passwordEncoder()); // So sánh mật khẩu đã mã hóa
        return provider;
    }

    // Bean cung cấp PasswordEncoder dùng thuật toán BCrypt để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); // Mã hóa bảo mật cao
    }

    // Bean cấu hình SecurityFilterChain - định nghĩa các rule bảo mật chính
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Tắt bảo vệ CSRF (có thể bật lại nếu dùng form POST)
                .formLogin(httpForm -> {
                    httpForm.loginPage("/req/login").permitAll(); // Trang login tùy chỉnh
                    httpForm.defaultSuccessUrl("/index"); // Sau login thành công, chuyển về /index
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")                      // Đường dẫn logout
                        .logoutSuccessUrl("/req/login?logout")      // Trang sau khi logout thành công
                        .invalidateHttpSession(true)               // Hủy phiên (session)
                        .deleteCookies("JSESSIONID")               // Xóa cookie phiên
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                )
                .authorizeHttpRequests(registry -> {

                    // Các URL public
                    registry.requestMatchers("/req/signup", "/css/**", "/js/**").permitAll();

                    // Quyền admin/user cho interns như hiện tại
                    registry.requestMatchers("/interns", "/interns/**").hasAnyRole("ADMIN", "USER");
                    registry.requestMatchers("/interns/add", "/interns/edit/**", "/interns/delete/**").hasRole("ADMIN");

                    // Sản phẩm
                    registry.requestMatchers("/products", "/products/**").permitAll();
                    registry.requestMatchers("/product/products/add", "/product/products/edit/**", "/product/products/delete/**").hasRole("ADMIN");

                    // Quản lý user
                    registry.requestMatchers("/users", "/users/add", "/users/edit/**", "/users/delete/**").hasRole("ADMIN");

                    // Cuộc họp
                    registry.requestMatchers("/meetings").permitAll();
                    registry.requestMatchers("/meetings/create", "/meetings/edit/**", "/meetings/delete/**").hasRole("ADMIN");

                    // ** Thêm quyền admin cho dự án: **
                    registry.requestMatchers("/admin/projects/**").hasRole("ADMIN");

                    // Các URL khác yêu cầu đăng nhập
                    registry.anyRequest().authenticated();
                })

                .build(); // Kết thúc cấu hình
    }
}