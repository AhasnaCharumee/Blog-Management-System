package lk.ijse.gdse72.blog_management.service;

import lk.ijse.gdse72.blog_management.dto.CommentDTO;
import lk.ijse.gdse72.blog_management.entity.Comment;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.exceptions.ResourceNotFound;
import lk.ijse.gdse72.blog_management.repository.CommentRepository;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentDTO createComment(Long postId, String userEmail, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id " + postId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFound("User not found with email " + userEmail));

        Comment comment = new Comment(null, content, user, post, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return new CommentDTO(savedComment.getId(), savedComment.getContent(), savedComment.getUser().getName(), savedComment.getCreatedDate());
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id " + postId));
        List<Comment> comments = commentRepository.findByPostOrderByCreatedDateDesc(post);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getId(), comment.getContent(), comment.getUser().getName(), comment.getCreatedDate()))
                .collect(Collectors.toList());
    }
}