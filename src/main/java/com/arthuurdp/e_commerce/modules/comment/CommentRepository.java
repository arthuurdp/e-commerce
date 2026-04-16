package com.arthuurdp.e_commerce.modules.comment;

import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByReviewId(Long reviewId);
    List<Comment> findByProductId(Long productId);
}
