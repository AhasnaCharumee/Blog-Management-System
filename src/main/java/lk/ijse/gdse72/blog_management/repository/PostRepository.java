package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // src/main/java/lk/ijse/gdse72/blog_management/repository/PostRepository.java
    List<Post> findByStatus(PostStatus status);
}