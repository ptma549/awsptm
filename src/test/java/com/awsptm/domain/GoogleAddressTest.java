package com.awsptm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GoogleAddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoogleAddress.class);
        GoogleAddress googleAddress1 = new GoogleAddress();
        googleAddress1.setId(1L);
        GoogleAddress googleAddress2 = new GoogleAddress();
        googleAddress2.setId(googleAddress1.getId());
        assertThat(googleAddress1).isEqualTo(googleAddress2);
        googleAddress2.setId(2L);
        assertThat(googleAddress1).isNotEqualTo(googleAddress2);
        googleAddress1.setId(null);
        assertThat(googleAddress1).isNotEqualTo(googleAddress2);
    }
}
