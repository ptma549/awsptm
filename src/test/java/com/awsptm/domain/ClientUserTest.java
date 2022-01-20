package com.awsptm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientUser.class);
        ClientUser clientUser1 = new ClientUser();
        clientUser1.setId(1L);
        ClientUser clientUser2 = new ClientUser();
        clientUser2.setId(clientUser1.getId());
        assertThat(clientUser1).isEqualTo(clientUser2);
        clientUser2.setId(2L);
        assertThat(clientUser1).isNotEqualTo(clientUser2);
        clientUser1.setId(null);
        assertThat(clientUser1).isNotEqualTo(clientUser2);
    }
}
