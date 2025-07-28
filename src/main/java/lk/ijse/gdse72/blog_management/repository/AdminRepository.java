package lk.ijse.gdse72.blog_management.repository;
import lk.ijse.gdse72.blog_management.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<AdminEntity, Long> {
    boolean existsByUsername(String username);
}