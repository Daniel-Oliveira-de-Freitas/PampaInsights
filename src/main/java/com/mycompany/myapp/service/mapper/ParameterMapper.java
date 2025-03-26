package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Parameter;
import com.mycompany.myapp.domain.Search;
import com.mycompany.myapp.service.dto.ParameterDTO;
import com.mycompany.myapp.service.dto.SearchDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Parameter} and its DTO {@link ParameterDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParameterMapper extends EntityMapper<ParameterDTO, Parameter> {
    @Mapping(target = "search", source = "search", qualifiedByName = "searchId")
    ParameterDTO toDto(Parameter s);

    @Named("searchId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SearchDTO toDtoSearchId(Search search);
}
