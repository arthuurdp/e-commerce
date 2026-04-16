package com.arthuurdp.e_commerce.modules.comment.mapper;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponse;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import com.arthuurdp.e_commerce.modules.review.mapper.ReviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(comment.getUser().getFirstName() + \" \" + comment.getUser().getLastName())")
    @Mapping(target = "productId", source = "product.id")
    CommentResponse toCommentResponseDTO(Comment comment);

    List<CommentResponse> toCommentResponseDTOList(List<Comment> comments);
}
