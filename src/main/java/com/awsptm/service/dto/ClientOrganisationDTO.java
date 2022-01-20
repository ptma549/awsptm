package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.ClientOrganisation} entity.
 */
@Schema(description = "Allows groups of users to be defined by organisation.")
public class ClientOrganisationDTO implements Serializable {

    private Long id;

    private String name;

    private String domain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientOrganisationDTO)) {
            return false;
        }

        ClientOrganisationDTO clientOrganisationDTO = (ClientOrganisationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clientOrganisationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientOrganisationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", domain='" + getDomain() + "'" +
            "}";
    }
}
