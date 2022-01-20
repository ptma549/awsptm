package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientUserMapperTest {

    private ClientUserMapper clientUserMapper;

    @BeforeEach
    public void setUp() {
        clientUserMapper = new ClientUserMapperImpl();
    }
}
