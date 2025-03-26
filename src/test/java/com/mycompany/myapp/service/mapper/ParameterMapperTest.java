package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ParameterAsserts.*;
import static com.mycompany.myapp.domain.ParameterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterMapperTest {

    private ParameterMapper parameterMapper;

    @BeforeEach
    void setUp() {
        parameterMapper = new ParameterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParameterSample1();
        var actual = parameterMapper.toEntity(parameterMapper.toDto(expected));
        assertParameterAllPropertiesEquals(expected, actual);
    }
}
