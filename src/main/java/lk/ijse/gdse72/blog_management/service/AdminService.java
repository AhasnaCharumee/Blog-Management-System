package lk.ijse.gdse72.blog_management.service;

import lk.ijse.gdse72.blog_management.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    AdminDTO save(AdminDTO dto);
    List<AdminDTO> findAll();
    AdminDTO findById(Long id);
    void delete(Long id);
}