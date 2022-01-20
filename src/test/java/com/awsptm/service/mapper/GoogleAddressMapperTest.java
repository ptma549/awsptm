package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GoogleAddressMapperTest {

    private GoogleAddressMapper googleAddressMapper;

    @BeforeEach
    public void setUp() {
        googleAddressMapper = new GoogleAddressMapperImpl();
    }
}
