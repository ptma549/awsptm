package com.awsptm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientOrganisationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientOrganisation.class);
        ClientOrganisation clientOrganisation1 = new ClientOrganisation();
        clientOrganisation1.setId(1L);
        ClientOrganisation clientOrganisation2 = new ClientOrganisation();
        clientOrganisation2.setId(clientOrganisation1.getId());
        assertThat(clientOrganisation1).isEqualTo(clientOrganisation2);
        clientOrganisation2.setId(2L);
        assertThat(clientOrganisation1).isNotEqualTo(clientOrganisation2);
        clientOrganisation1.setId(null);
        assertThat(clientOrganisation1).isNotEqualTo(clientOrganisation2);
    }
}
