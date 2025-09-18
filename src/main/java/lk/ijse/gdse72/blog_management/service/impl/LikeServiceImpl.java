package lk.ijse.gdse72.blog_management.service.impl;

import lk.ijse.gdse72.blog_management.dto.LikeDTO;
import lk.ijse.gdse72.blog_management.entity.Like;
import lk.ijse.gdse72.blog_management.entity.Post;
import lk.ijse.gdse72.blog_management.entity.User;
import lk.ijse.gdse72.blog_management.exceptions.ResourceNotFound;
import lk.ijse.gdse72.blog_management.repository.LikeRepository;
import lk.ijse.gdse72.blog_management.repository.PostRepository;
import lk.ijse.gdse72.blog_management.repository.UserRepository;
import lk.ijse.gdse72.blog_management.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public LikeDTO toggleLike(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id " + postId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFound("User not found with email " + userEmail));

        return likeRepository.findByUserAndPost(user, post)
                .map(like -> {
                    // Unlike the post
                    likeRepository.delete(like);
                    post.setLikes(post.getLikes() - 1);
                    postRepository.save(post);
                    return new LikeDTO(); // Or an appropriate DTO for unliked state
                })
                .orElseGet(() -> {
                    // Like the post
                    Like newLike = new Like(null, user, post);
                    likeRepository.save(newLike);
                    post.setLikes(post.getLikes() + 1);
                    postRepository.save(post);
                    return new LikeDTO(); // Or an appropriate DTO for liked state
                });
    }
    public boolean hasUserLikedPost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id " + postId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFound("User not found with email " + userEmail));
        return likeRepository.findByUserAndPost(user, post).isPresent();
    }

}