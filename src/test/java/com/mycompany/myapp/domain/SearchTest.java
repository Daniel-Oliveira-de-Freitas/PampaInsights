package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SearchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Search.class);
        Search search1 = getSearchSample1();
        Search search2 = new Search();
        assertThat(search1).isNotEqualTo(search2);

        search2.setId(search1.getId());
        assertThat(search1).isEqualTo(search2);

        search2 = getSearchSample2();
        assertThat(search1).isNotEqualTo(search2);
    }
}
