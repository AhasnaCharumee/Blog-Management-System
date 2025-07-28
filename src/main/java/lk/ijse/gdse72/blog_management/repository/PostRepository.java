package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}