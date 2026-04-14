package com.arthuurdp.e_commerce.modules.comment.controller;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponseDTO;
import com.arthuurdp.e_commerce.modules.comment.dtos.UpdateCommentDTO;
import com.arthuurdp.e_commerce.modules.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(commentService.createComment(commentDTO, authenticatedUser.getUser()));
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDTO updateCommentDTO,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, updateCommentDTO, authenticatedUser.getUser()));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        commentService.deleteComment(commentId, authenticatedUser.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getProductComments(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getProductComments(productId));
    }
}
