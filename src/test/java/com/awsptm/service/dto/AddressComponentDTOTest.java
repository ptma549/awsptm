package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressComponentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AddressComponentDTO.class);
        AddressComponentDTO addressComponentDTO1 = new AddressComponentDTO();
        addressComponentDTO1.setId(1L);
        AddressComponentDTO addressComponentDTO2 = new AddressComponentDTO();
        assertThat(addressComponentDTO1).isNotEqualTo(addressComponentDTO2);
        addressComponentDTO2.setId(addressComponentDTO1.getId());
        assertThat(addressComponentDTO1).isEqualTo(addressComponentDTO2);
        addressComponentDTO2.setId(2L);
        assertThat(addressComponentDTO1).isNotEqualTo(addressComponentDTO2);
        addressComponentDTO1.setId(null);
        assertThat(addressComponentDTO1).isNotEqualTo(addressComponentDTO2);
    }
}
