package com.arthuurdp.e_commerce.modules.comment;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponseDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.UpdateCommentDTO;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import com.arthuurdp.e_commerce.modules.comment.mapper.CommentMapper;
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
    private final CommentMapper mapper;

    public CommentService(CommentRepository commentRepository, ProductRepository productRepository, CommentMapper mapper) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CommentResponseDTO createComment(CommentDTO commentDTO, User user) {
        Product product = productRepository.findById(commentDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Comment comment = new Comment(user, product, commentDTO.getContent());
        return mapper.toCommentResponseDTO(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, UpdateCommentDTO updateCommentDTO, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own comments");
        }

        comment.setContent(updateCommentDTO.getContent());
        return mapper.toCommentResponseDTO(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getProductComments(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        return mapper.toCommentResponseDTOList(commentRepository.findByProductId(productId));
    }
}
