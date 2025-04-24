package com.example.demo.Repository;

import com.example.demo.Entity.Intern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternRepository extends JpaRepository<Intern, Integer> {

}
