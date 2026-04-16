package com.arthuurdp.e_commerce.modules.comment;

import com.arthuurdp.e_commerce.infrastructure.security.UserAuthenticated;
import com.arthuurdp.e_commerce.modules.comment.dtos.CreateCommentRequest;
import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.comment.dtos.UpdateCommentRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/reviews")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest req,
            @AuthenticationPrincipal UserAuthenticated authenticatedUser
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, req, authenticatedUser.getUser()));
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
    public ResponseEntity<List<CommentResponse>> getProductComments(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getProductComments(productId));
    }
}
