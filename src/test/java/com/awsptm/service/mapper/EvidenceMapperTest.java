package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceMapperTest {

    private EvidenceMapper evidenceMapper;

    @BeforeEach
    public void setUp() {
        evidenceMapper = new EvidenceMapperImpl();
    }
}
