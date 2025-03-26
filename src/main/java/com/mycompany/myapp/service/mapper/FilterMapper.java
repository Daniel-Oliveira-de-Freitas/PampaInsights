package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Filter;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.service.dto.FilterDTO;
import com.mycompany.myapp.service.dto.SearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Filter} and its DTO {@link FilterDTO}.
 */
@Mapper(componentModel = "spring")
public interface FilterMapper extends EntityMapper<FilterDTO, Filter> {
    @Mapping(target = "search", source = "search", qualifiedByName = "searchId")
    FilterDTO toDto(Filter s);

    @Named("searchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SearchDTO toDtoSearchId(Search search);
}
