package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InspectionMapperTest {

    private InspectionMapper inspectionMapper;

    @BeforeEach
    public void setUp() {
        inspectionMapper = new InspectionMapperImpl();
    }
}
