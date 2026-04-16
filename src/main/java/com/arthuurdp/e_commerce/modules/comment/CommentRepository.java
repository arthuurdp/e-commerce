package com.arthuurdp.e_commerce.modules.comment;

import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByReviewId(Long reviewId);
    Optional<List<Comment>> findByUserId(Long userId);
    List<Comment> findByProductId(Long productId);
}
