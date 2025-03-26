package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.FilterAsserts.*;
import static com.mycompany.myapp.domain.FilterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilterMapperTest {

    private FilterMapper filterMapper;

    @BeforeEach
    void setUp() {
        filterMapper = new FilterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFilterSample1();
        var actual = filterMapper.toEntity(filterMapper.toDto(expected));
        assertFilterAllPropertiesEquals(expected, actual);
    }
}
