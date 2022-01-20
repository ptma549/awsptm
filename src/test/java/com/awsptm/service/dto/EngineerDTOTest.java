package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngineerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EngineerDTO.class);
        EngineerDTO engineerDTO1 = new EngineerDTO();
        engineerDTO1.setId(1L);
        EngineerDTO engineerDTO2 = new EngineerDTO();
        assertThat(engineerDTO1).isNotEqualTo(engineerDTO2);
        engineerDTO2.setId(engineerDTO1.getId());
        assertThat(engineerDTO1).isEqualTo(engineerDTO2);
        engineerDTO2.setId(2L);
        assertThat(engineerDTO1).isNotEqualTo(engineerDTO2);
        engineerDTO1.setId(null);
        assertThat(engineerDTO1).isNotEqualTo(engineerDTO2);
    }
}
