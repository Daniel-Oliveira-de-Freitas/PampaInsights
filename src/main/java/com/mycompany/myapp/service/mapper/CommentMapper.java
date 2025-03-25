package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Comment;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.service.dto.CommentDTO;
import com.mycompany.myapp.service.dto.SearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "search", source = "search", qualifiedByName = "searchId")
    CommentDTO toDto(Comment s);

    @Named("searchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SearchDTO toDtoSearchId(Search search);
}
