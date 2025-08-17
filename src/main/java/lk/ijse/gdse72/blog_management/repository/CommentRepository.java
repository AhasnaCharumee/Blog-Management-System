package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.Comment;
import lk.ijse.gdse72.blog_management.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedDateDesc(Post post);
    long countByPost(Post post);
}