// src/main/java/lk/ijse/gdse72/blog_management/service/impl/PostServiceImpl.java
package lk.ijse.gdse72.blog_management.service.impl;
import jakarta.persistence.EntityNotFoundException;
import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.service.EmailService;
import lk.ijse.gdse72.blog_management.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final String uploadDir = "uploads/";

    @Override
    public PostDTO createPost(PostDTO postDTO, MultipartFile image, User user) {
        String authorName = user.getName() != null ? user.getName() : user.getEmail();

        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = saveImage(image);
        }

        Post post = Post.builder()
                .title(postDTO.getTitle())
                .content(postDTO.getContent())
                .author(authorName)
                .createdDate(postDTO.getCreatedDate() != null ? postDTO.getCreatedDate() : LocalDateTime.now())
                .imagePath(imagePath)
                .status(PostStatus.PENDING)
                .user(user) // Link to authenticated user
                .views(0)
                .likes(0)
                .commentsCount(0)
                .build();

        Post saved = postRepository.save(post);
        return toDTO(saved);
    }



    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return toDTO(post);
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found"));

        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        if (image != null && !image.isEmpty()) {
            post.setImagePath(saveImage(image));
        }
        // Optionally update status, etc.
        postRepository.save(post);
        return toDTO(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public PostDTO approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        post.setStatus(PostStatus.APPROVED);
        postRepository.save(post);

        // Send email notification to the post owner
        if (post.getUser() != null && post.getUser().getEmail() != null) {
            String email = post.getUser().getEmail();
            String subject = "Your Post is Approved!";
            String message = "Hello " + post.getUser().getName() + ",\n\n" +
                    "Your post titled \"" + post.getTitle() + "\" has been approved and is now live on BlogSphere.\n\n" +
                    "Best Regards,\nBlogSphere Team";
            emailService.sendLoginSuccessEmail(email, subject, message);
        }

        return toDTO(post);
    }

    @Override
    public PostDTO rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        post.setStatus(PostStatus.REJECTED);
        postRepository.save(post);
        return toDTO(post);
    }
    @Override
    public List<PostDTO> getAllPostsForAdmin() {
        return postRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updatePost(Long postId, PostDTO postDTO, Long authenticatedUserId) {
        // Implement as needed
    }

    @Override
    public List<PostDTO> getPostsByUser(User user) {
        return postRepository.findByUser(user).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String saveImage(MultipartFile image) {
        try {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    private PostDTO toDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedDate(),
                post.getImagePath(),
                post.getStatus(),
                post.getViews(),
                post.getLikes(),
                post.getCommentsCount()
        );
    }
    @Override
    public Map<String, Object> getUserPostInteractionStats(User user) {
        List<Post> userPosts = postRepository.findByUser(user);
        int totalLikes = userPosts.stream().mapToInt(Post::getLikes).sum();
        int totalComments = userPosts.stream().mapToInt(Post::getCommentsCount).sum();
        int totalViews = userPosts.stream().mapToInt(Post::getViews).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLikes", totalLikes);
        stats.put("totalComments", totalComments);
        stats.put("totalViews", totalViews);
        return stats;
    }

    @Override
    public Page<PostDTO> searchPostsByTitle(String title, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::toDTO);
    }
}