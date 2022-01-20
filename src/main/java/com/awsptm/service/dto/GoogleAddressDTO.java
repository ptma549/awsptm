package com.awsptm.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.awsptm.domain.GoogleAddress} entity.
 */
@Schema(description = "The response from Google")
public class GoogleAddressDTO implements Serializable {

    private Long id;

    private String position;

    private String url;

    private String html;

    private String formatted;

    private String types;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GoogleAddressDTO)) {
            return false;
        }

        GoogleAddressDTO googleAddressDTO = (GoogleAddressDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, googleAddressDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GoogleAddressDTO{" +
            "id=" + getId() +
            ", position='" + getPosition() + "'" +
            ", url='" + getUrl() + "'" +
            ", html='" + getHtml() + "'" +
            ", formatted='" + getFormatted() + "'" +
            ", types='" + getTypes() + "'" +
            "}";
    }
}
