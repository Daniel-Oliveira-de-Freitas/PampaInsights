package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.service.dto.CommentDTO;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySearchId(Long searchId);
}
