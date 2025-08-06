package lk.ijse.gdse72.blog_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private String email;
    private String name;
}