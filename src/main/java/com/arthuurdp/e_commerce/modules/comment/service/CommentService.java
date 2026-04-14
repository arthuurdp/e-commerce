package com.arthuurdp.e_commerce.modules.comment.service;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponseDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.UpdateCommentDTO;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import com.arthuurdp.e_commerce.modules.comment.repository.CommentRepository;
import com.arthuurdp.e_commerce.modules.product.ProductRepository;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.shared.exceptions.AccessDeniedException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;

    public CommentService(CommentRepository commentRepository, ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CommentResponseDTO createComment(CommentDTO commentDTO, User user) {
        Product product = productRepository.findById(commentDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Comment comment = new Comment(user, product, commentDTO.getContent());
        Comment savedComment = commentRepository.save(comment);

        return mapToResponseDTO(savedComment);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, UpdateCommentDTO updateCommentDTO, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comments");
        }

        comment.setContent(updateCommentDTO.getContent());
        Comment updatedComment = commentRepository.save(comment);

        return mapToResponseDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDTO> getProductComments(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return commentRepository.findByProductId(productId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private CommentResponseDTO mapToResponseDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUserName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
        dto.setProductId(comment.getProduct().getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
