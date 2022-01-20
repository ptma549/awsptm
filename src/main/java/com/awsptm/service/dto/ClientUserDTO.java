package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.ClientUser} entity.
 */
@Schema(description = "A proxy object to the User object created by JHipster\nto allow associations to it.")
public class ClientUserDTO implements Serializable {

    private Long id;

    private String landline;

    private String mobile;

    private UserDTO user;

    private ClientOrganisationDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ClientOrganisationDTO getClient() {
        return client;
    }

    public void setClient(ClientOrganisationDTO client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientUserDTO)) {
            return false;
        }

        ClientUserDTO clientUserDTO = (ClientUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clientUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientUserDTO{" +
            "id=" + getId() +
            ", landline='" + getLandline() + "'" +
            ", mobile='" + getMobile() + "'" +
            ", user=" + getUser() +
            ", client=" + getClient() +
            "}";
    }
}
