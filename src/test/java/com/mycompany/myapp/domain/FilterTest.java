package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FilterTestSamples.*;
import static com.mycompany.myapp.domain.SearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FilterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Filter.class);
        Filter filter1 = getFilterSample1();
        Filter filter2 = new Filter();
        assertThat(filter1).isNotEqualTo(filter2);

        filter2.setId(filter1.getId());
        assertThat(filter1).isEqualTo(filter2);

        filter2 = getFilterSample2();
        assertThat(filter1).isNotEqualTo(filter2);
    }

    @Test
    void searchTest() {
        Filter filter = getFilterRandomSampleGenerator();
        Search searchBack = getSearchRandomSampleGenerator();

        filter.setSearch(searchBack);
        assertThat(filter.getSearch()).isEqualTo(searchBack);

        filter.search(null);
        assertThat(filter.getSearch()).isNull();
    }
}
