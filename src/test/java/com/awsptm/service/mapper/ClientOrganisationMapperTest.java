package com.awsptm.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientOrganisationMapperTest {

    private ClientOrganisationMapper clientOrganisationMapper;

    @BeforeEach
    public void setUp() {
        clientOrganisationMapper = new ClientOrganisationMapperImpl();
    }
}
