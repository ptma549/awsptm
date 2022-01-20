package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddressTypeMapperTest {

    private AddressTypeMapper addressTypeMapper;

    @BeforeEach
    public void setUp() {
        addressTypeMapper = new AddressTypeMapperImpl();
    }
}
