package com.example.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;



@Entity
@Table(name = "interns") // tên bảng trong DB
public class Intern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String hoTen;

    private Integer namSinh;

    private LocalDate thoiGianBatDau;

    private Integer thoiGianLam;

    private Float diemDanhGia;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Integer getNamSinh() { return namSinh; }
    public void setNamSinh(Integer namSinh) { this.namSinh = namSinh; }

    public LocalDate getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(LocalDate thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }

    public Integer getThoiGianLam() { return thoiGianLam; }
    public void setThoiGianLam(Integer thoiGianLam) { this.thoiGianLam = thoiGianLam; }

    public Float getDiemDanhGia() { return diemDanhGia; }
    public void setDiemDanhGia(Float diemDanhGia) { this.diemDanhGia = diemDanhGia; }
}
