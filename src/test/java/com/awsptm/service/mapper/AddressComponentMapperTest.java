package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressComponentMapperTest {

    private AddressComponentMapper addressComponentMapper;

    @BeforeEach
    public void setUp() {
        addressComponentMapper = new AddressComponentMapperImpl();
    }
}
