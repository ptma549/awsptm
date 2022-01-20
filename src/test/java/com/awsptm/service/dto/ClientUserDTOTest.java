package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientUserDTO.class);
        ClientUserDTO clientUserDTO1 = new ClientUserDTO();
        clientUserDTO1.setId(1L);
        ClientUserDTO clientUserDTO2 = new ClientUserDTO();
        assertThat(clientUserDTO1).isNotEqualTo(clientUserDTO2);
        clientUserDTO2.setId(clientUserDTO1.getId());
        assertThat(clientUserDTO1).isEqualTo(clientUserDTO2);
        clientUserDTO2.setId(2L);
        assertThat(clientUserDTO1).isNotEqualTo(clientUserDTO2);
        clientUserDTO1.setId(null);
        assertThat(clientUserDTO1).isNotEqualTo(clientUserDTO2);
    }
}
