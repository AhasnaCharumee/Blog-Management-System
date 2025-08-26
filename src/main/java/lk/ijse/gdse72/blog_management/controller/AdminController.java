package lk.ijse.gdse72.blog_management.controller;

import lk.ijse.gdse72.blog_management.dto.PostDTO;
import lk.ijse.gdse72.blog_management.service.PostService;
import lk.ijse.gdse72.blog_management.utility.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<APIResponse<List<PostDTO>>> getAllPostsForAdmin() {
        List<PostDTO> posts = postService.getAllPostsForAdmin();
        return ResponseEntity.ok(new APIResponse<>(200, "All posts for admin", posts));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<APIResponse<PostDTO>> approvePost(@PathVariable Long id) {
        PostDTO post = postService.approvePost(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Post approved", post));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<APIResponse<PostDTO>> rejectPost(@PathVariable Long id) {
        PostDTO post = postService.rejectPost(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Post rejected", post));
    }
}