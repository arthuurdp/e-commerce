package com.arthuurdp.e_commerce.modules.comment.mapper;

import com.arthuurdp.e_commerce.modules.comment.dtos.CommentResponseDTO;
import com.arthuurdp.e_commerce.modules.comment.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(comment.getUser().getFirstName() + \" \" + comment.getUser().getLastName())")
    @Mapping(target = "productId", source = "product.id")
    CommentResponseDTO toCommentResponseDTO(Comment comment);

    List<CommentResponseDTO> toCommentResponseDTOList(List<Comment> comments);
}