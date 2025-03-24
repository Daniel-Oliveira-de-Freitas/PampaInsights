package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SearchAsserts.*;
import static com.mycompany.myapp.domain.SearchTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchMapperTest {

    private SearchMapper searchMapper;

    @BeforeEach
    void setUp() {
        searchMapper = new SearchMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSearchSample1();
        var actual = searchMapper.toEntity(searchMapper.toDto(expected));
        assertSearchAllPropertiesEquals(expected, actual);
    }
}
