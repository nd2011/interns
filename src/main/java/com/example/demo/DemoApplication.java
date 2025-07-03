package com.example.demo;

import com.example.demo.Entity.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdmin(MyAppUserRepository userRepository) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				MyAppUser admin = new MyAppUser();
				admin.setUsername("admin");
				admin.setFullname("Administrator");
				admin.setYear("2000");
				admin.setEmail("admin@example.com");
				admin.setPassword(new BCryptPasswordEncoder().encode("123"));
				admin.setJoindate("2025-06-27");
				admin.setRoles(Set.of("ROLE_ADMIN"));

				userRepository.save(admin);
				System.out.println("✅ Tạo tài khoản admin mặc định");
			}
		};
	}
}
