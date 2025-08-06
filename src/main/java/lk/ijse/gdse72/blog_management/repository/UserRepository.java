package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}