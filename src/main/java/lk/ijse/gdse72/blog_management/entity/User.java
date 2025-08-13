// src/main/java/lk/ijse/gdse72/blog_management/entity/User.java
package lk.ijse.gdse72.blog_management.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password; // Add password for authentication
}