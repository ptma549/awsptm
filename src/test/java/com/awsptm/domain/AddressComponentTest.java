package com.awsptm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressComponentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AddressComponent.class);
        AddressComponent addressComponent1 = new AddressComponent();
        addressComponent1.setId(1L);
        AddressComponent addressComponent2 = new AddressComponent();
        addressComponent2.setId(addressComponent1.getId());
        assertThat(addressComponent1).isEqualTo(addressComponent2);
        addressComponent2.setId(2L);
        assertThat(addressComponent1).isNotEqualTo(addressComponent2);
        addressComponent1.setId(null);
        assertThat(addressComponent1).isNotEqualTo(addressComponent2);
    }
}
