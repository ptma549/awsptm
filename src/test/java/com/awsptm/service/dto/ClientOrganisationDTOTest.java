package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientOrganisationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientOrganisationDTO.class);
        ClientOrganisationDTO clientOrganisationDTO1 = new ClientOrganisationDTO();
        clientOrganisationDTO1.setId(1L);
        ClientOrganisationDTO clientOrganisationDTO2 = new ClientOrganisationDTO();
        assertThat(clientOrganisationDTO1).isNotEqualTo(clientOrganisationDTO2);
        clientOrganisationDTO2.setId(clientOrganisationDTO1.getId());
        assertThat(clientOrganisationDTO1).isEqualTo(clientOrganisationDTO2);
        clientOrganisationDTO2.setId(2L);
        assertThat(clientOrganisationDTO1).isNotEqualTo(clientOrganisationDTO2);
        clientOrganisationDTO1.setId(null);
        assertThat(clientOrganisationDTO1).isNotEqualTo(clientOrganisationDTO2);
    }
}
