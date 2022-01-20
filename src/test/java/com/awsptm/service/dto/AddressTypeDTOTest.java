package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AddressTypeDTO.class);
        AddressTypeDTO addressTypeDTO1 = new AddressTypeDTO();
        addressTypeDTO1.setId(1L);
        AddressTypeDTO addressTypeDTO2 = new AddressTypeDTO();
        assertThat(addressTypeDTO1).isNotEqualTo(addressTypeDTO2);
        addressTypeDTO2.setId(addressTypeDTO1.getId());
        assertThat(addressTypeDTO1).isEqualTo(addressTypeDTO2);
        addressTypeDTO2.setId(2L);
        assertThat(addressTypeDTO1).isNotEqualTo(addressTypeDTO2);
        addressTypeDTO1.setId(null);
        assertThat(addressTypeDTO1).isNotEqualTo(addressTypeDTO2);
    }
}
