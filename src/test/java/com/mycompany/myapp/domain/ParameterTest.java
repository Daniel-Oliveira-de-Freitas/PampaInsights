package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ParameterTestSamples.*;
import static com.mycompany.myapp.domain.SearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParameterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Parameter.class);
        Parameter parameter1 = getParameterSample1();
        Parameter parameter2 = new Parameter();
        assertThat(parameter1).isNotEqualTo(parameter2);

        parameter2.setId(parameter1.getId());
        assertThat(parameter1).isEqualTo(parameter2);

        parameter2 = getParameterSample2();
        assertThat(parameter1).isNotEqualTo(parameter2);
    }

    @Test
    void searchTest() {
        Parameter parameter = getParameterRandomSampleGenerator();
        Search searchBack = getSearchRandomSampleGenerator();

        parameter.setSearch(searchBack);
        assertThat(parameter.getSearch()).isEqualTo(searchBack);

        parameter.search(null);
        assertThat(parameter.getSearch()).isNull();
    }
}
