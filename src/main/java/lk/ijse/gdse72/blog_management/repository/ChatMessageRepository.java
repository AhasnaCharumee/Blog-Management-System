// src/main/java/lk/ijse/gdse72/blog_management/repository/ChatMessageRepository.java
package lk.ijse.gdse72.blog_management.repository;

import lk.ijse.gdse72.blog_management.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
}
