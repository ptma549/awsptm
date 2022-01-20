package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CertificateMapperTest {

    private CertificateMapper certificateMapper;

    @BeforeEach
    public void setUp() {
        certificateMapper = new CertificateMapperImpl();
    }
}
