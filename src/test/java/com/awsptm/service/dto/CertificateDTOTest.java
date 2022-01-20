package com.awsptm.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.awsptm.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CertificateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateDTO.class);
        CertificateDTO certificateDTO1 = new CertificateDTO();
        certificateDTO1.setId(1L);
        CertificateDTO certificateDTO2 = new CertificateDTO();
        assertThat(certificateDTO1).isNotEqualTo(certificateDTO2);
        certificateDTO2.setId(certificateDTO1.getId());
        assertThat(certificateDTO1).isEqualTo(certificateDTO2);
        certificateDTO2.setId(2L);
        assertThat(certificateDTO1).isNotEqualTo(certificateDTO2);
        certificateDTO1.setId(null);
        assertThat(certificateDTO1).isNotEqualTo(certificateDTO2);
    }
}
