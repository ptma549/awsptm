package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EngineerMapperTest {

    private EngineerMapper engineerMapper;

    @BeforeEach
    public void setUp() {
        engineerMapper = new EngineerMapperImpl();
    }
}
