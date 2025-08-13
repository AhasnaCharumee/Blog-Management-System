package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}