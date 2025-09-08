package lk.ijse.gdse72.blog_management.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users/images")
public class ImageController {

    private final Path uploadDir = Paths.get("C:/Users/ahasna/Documents/AAD/SPIRING-BOOT/Blog_Management/uploads/");

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path filePath = uploadDir.resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());
        if(resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok().body(resource);
        } else {
            // fallback default image
            Path defaultPath = uploadDir.resolve("default.png");
            Resource defaultResource = new UrlResource(defaultPath.toUri());
            return ResponseEntity.ok().body(defaultResource);
        }
    }
}
