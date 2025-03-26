package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParameterDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParameterDTO.class);
        ParameterDTO parameterDTO1 = new ParameterDTO();
        parameterDTO1.setId(1L);
        ParameterDTO parameterDTO2 = new ParameterDTO();
        assertThat(parameterDTO1).isNotEqualTo(parameterDTO2);
        parameterDTO2.setId(parameterDTO1.getId());
        assertThat(parameterDTO1).isEqualTo(parameterDTO2);
        parameterDTO2.setId(2L);
        assertThat(parameterDTO1).isNotEqualTo(parameterDTO2);
        parameterDTO1.setId(null);
        assertThat(parameterDTO1).isNotEqualTo(parameterDTO2);
    }
}
