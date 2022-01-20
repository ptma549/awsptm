package com.awsptm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngineerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Engineer.class);
        Engineer engineer1 = new Engineer();
        engineer1.setId(1L);
        Engineer engineer2 = new Engineer();
        engineer2.setId(engineer1.getId());
        assertThat(engineer1).isEqualTo(engineer2);
        engineer2.setId(2L);
        assertThat(engineer1).isNotEqualTo(engineer2);
        engineer1.setId(null);
        assertThat(engineer1).isNotEqualTo(engineer2);
    }
}
