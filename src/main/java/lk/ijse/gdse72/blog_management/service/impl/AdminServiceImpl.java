package lk.ijse.gdse72.blog_management.service.impl;

import lk.ijse.gdse72.blog_management.dto.AdminDTO;
import lk.ijse.gdse72.blog_management.entity.AdminEntity;
import lk.ijse.gdse72.blog_management.repository.AdminRepository;
import lk.ijse.gdse72.blog_management.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository repo;

    private AdminDTO mapToDTO(AdminEntity entity) {
        return new AdminDTO(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRole());
    }

    private AdminEntity mapToEntity(AdminDTO dto) {
        return new AdminEntity(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getRole());
    }

    @Override
    public AdminDTO save(AdminDTO dto) {
        return mapToDTO(repo.save(mapToEntity(dto)));
    }

    @Override
    public List<AdminDTO> findAll() {
        return repo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AdminDTO findById(Long id) {
        return repo.findById(id).map(this::mapToDTO).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}