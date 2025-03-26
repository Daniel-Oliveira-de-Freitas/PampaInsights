package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.SearchDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Search} and its DTO {@link SearchDTO}.
 */
@Mapper(componentModel = "spring")
public interface SearchMapper extends EntityMapper<SearchDTO, Search> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    SearchDTO toDto(Search s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
