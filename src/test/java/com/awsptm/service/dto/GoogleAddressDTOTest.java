package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GoogleAddressDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoogleAddressDTO.class);
        GoogleAddressDTO googleAddressDTO1 = new GoogleAddressDTO();
        googleAddressDTO1.setId(1L);
        GoogleAddressDTO googleAddressDTO2 = new GoogleAddressDTO();
        assertThat(googleAddressDTO1).isNotEqualTo(googleAddressDTO2);
        googleAddressDTO2.setId(googleAddressDTO1.getId());
        assertThat(googleAddressDTO1).isEqualTo(googleAddressDTO2);
        googleAddressDTO2.setId(2L);
        assertThat(googleAddressDTO1).isNotEqualTo(googleAddressDTO2);
        googleAddressDTO1.setId(null);
        assertThat(googleAddressDTO1).isNotEqualTo(googleAddressDTO2);
    }
}
