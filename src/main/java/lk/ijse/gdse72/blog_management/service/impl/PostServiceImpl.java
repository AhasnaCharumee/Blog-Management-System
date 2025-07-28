package lk.ijse.gdse72.blog_management.service.impl;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.PostStatus;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final String UPLOAD_DIR = "uploads/"; // Directory to store images

    @Override
    public PostDTO createPost(PostDTO postDTO, MultipartFile image) {
        try {
            String imagePath = saveImage(image);
            Post post = Post.builder()
                    .title(postDTO.getTitle())
                    .content(postDTO.getContent())
                    .author(postDTO.getAuthor())
                    .createdDate(LocalDateTime.now())
                    .imagePath(imagePath)
                    .status(PostStatus.PENDING) // Set to PENDING
                    .build();
            Post savedPost = postRepository.save(post);
            return convertToDTO(savedPost);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .filter(post -> post.getStatus() == PostStatus.APPROVED) // Only approved
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public PostDTO getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToDTO(post);
    }

    @Override
    public PostDTO updatePost(Long id, PostDTO postDTO, MultipartFile image) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setAuthor(postDTO.getAuthor());

        if (image != null && !image.isEmpty()) {
            try {
                String newImagePath = saveImage(image);
                // Delete old image if it exists
                if (post.getImagePath() != null) {
                    Path oldImagePath = Paths.get(UPLOAD_DIR + post.getImagePath());
                    Files.deleteIfExists(oldImagePath);
                }
                post.setImagePath(newImagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to update image: " + e.getMessage());
            }
        }

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getImagePath() != null) {
            try {
                Path imagePath = Paths.get(UPLOAD_DIR + post.getImagePath());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image: " + e.getMessage());
            }
        }
        postRepository.delete(post);
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new RuntimeException("Uploaded image is empty");
        }
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, image.getBytes());
        return fileName; // Return relative path
    }

    // Update convertToDTO:
    private PostDTO convertToDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCreatedDate(),
                post.getImagePath(),
                post.getStatus() // Add status
        );
    }
    @Override
    public PostDTO approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setStatus(PostStatus.APPROVED);
        return convertToDTO(postRepository.save(post));
    }
    // In PostServiceImpl.java
    @Override
    public List<PostDTO> getAllPostsForAdmin() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public PostDTO rejectPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setStatus(PostStatus.REJECTED);
        return convertToDTO(postRepository.save(post));
    }
}