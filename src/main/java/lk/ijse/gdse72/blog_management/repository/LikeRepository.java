package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.Like;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    long countByPost(Post post);
}