package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.service.dto.SearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Search} and its DTO {@link SearchDTO}.
 */
@Mapper(componentModel = "spring")
public interface SearchMapper extends EntityMapper<SearchDTO, Search> {}
