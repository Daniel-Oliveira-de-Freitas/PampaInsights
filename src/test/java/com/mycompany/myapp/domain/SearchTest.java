package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CommentTestSamples.*;
import static com.mycompany.myapp.domain.FilterTestSamples.*;
import static com.mycompany.myapp.domain.ParameterTestSamples.*;
import static com.mycompany.myapp.domain.SearchTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
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

    @Test
    void filterTest() {
        Search search = getSearchRandomSampleGenerator();
        Filter filterBack = getFilterRandomSampleGenerator();

        search.setFilter(filterBack);
        assertThat(search.getFilter()).isEqualTo(filterBack);
        assertThat(filterBack.getSearch()).isEqualTo(search);

        search.filter(null);
        assertThat(search.getFilter()).isNull();
        assertThat(filterBack.getSearch()).isNull();
    }

    @Test
    void parameterTest() {
        Search search = getSearchRandomSampleGenerator();
        Parameter parameterBack = getParameterRandomSampleGenerator();

        search.setParameter(parameterBack);
        assertThat(search.getParameter()).isEqualTo(parameterBack);
        assertThat(parameterBack.getSearch()).isEqualTo(search);

        search.parameter(null);
        assertThat(search.getParameter()).isNull();
        assertThat(parameterBack.getSearch()).isNull();
    }

    @Test
    void commentTest() {
        Search search = getSearchRandomSampleGenerator();
        Comment commentBack = getCommentRandomSampleGenerator();

        search.addComment(commentBack);
        assertThat(search.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getSearch()).isEqualTo(search);

        search.removeComment(commentBack);
        assertThat(search.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getSearch()).isNull();

        search.comments(new HashSet<>(Set.of(commentBack)));
        assertThat(search.getComments()).containsOnly(commentBack);
        assertThat(commentBack.getSearch()).isEqualTo(search);

        search.setComments(new HashSet<>());
        assertThat(search.getComments()).doesNotContain(commentBack);
        assertThat(commentBack.getSearch()).isNull();
    }
}
