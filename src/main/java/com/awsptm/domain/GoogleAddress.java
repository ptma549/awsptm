package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * The response from Google
 */
@Entity
@Table(name = "google_address")
public class GoogleAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "position")
    private String position;

    @Column(name = "url")
    private String url;

    @Column(name = "html")
    private String html;

    @Column(name = "formatted")
    private String formatted;

    @Column(name = "types")
    private String types;

    @OneToMany(mappedBy = "address")
    @JsonIgnoreProperties(value = { "address", "type" }, allowSetters = true)
    private Set<AddressComponent> addressComponents = new HashSet<>();

    @JsonIgnoreProperties(value = { "googleAddress", "jobs", "inspections" }, allowSetters = true)
    @OneToOne(mappedBy = "googleAddress")
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GoogleAddress id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return this.position;
    }

    public GoogleAddress position(String position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrl() {
        return this.url;
    }

    public GoogleAddress url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return this.html;
    }

    public GoogleAddress html(String html) {
        this.setHtml(html);
        return this;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormatted() {
        return this.formatted;
    }

    public GoogleAddress formatted(String formatted) {
        this.setFormatted(formatted);
        return this;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getTypes() {
        return this.types;
    }

    public GoogleAddress types(String types) {
        this.setTypes(types);
        return this;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Set<AddressComponent> getAddressComponents() {
        return this.addressComponents;
    }

    public void setAddressComponents(Set<AddressComponent> addressComponents) {
        if (this.addressComponents != null) {
            this.addressComponents.forEach(i -> i.setAddress(null));
        }
        if (addressComponents != null) {
            addressComponents.forEach(i -> i.setAddress(this));
        }
        this.addressComponents = addressComponents;
    }

    public GoogleAddress addressComponents(Set<AddressComponent> addressComponents) {
        this.setAddressComponents(addressComponents);
        return this;
    }

    public GoogleAddress addAddressComponents(AddressComponent addressComponent) {
        this.addressComponents.add(addressComponent);
        addressComponent.setAddress(this);
        return this;
    }

    public GoogleAddress removeAddressComponents(AddressComponent addressComponent) {
        this.addressComponents.remove(addressComponent);
        addressComponent.setAddress(null);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        if (this.address != null) {
            this.address.setGoogleAddress(null);
        }
        if (address != null) {
            address.setGoogleAddress(this);
        }
        this.address = address;
    }

    public GoogleAddress address(Address address) {
        this.setAddress(address);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GoogleAddress)) {
            return false;
        }
        return id != null && id.equals(((GoogleAddress) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GoogleAddress{" +
            "id=" + getId() +
            ", position='" + getPosition() + "'" +
            ", url='" + getUrl() + "'" +
            ", html='" + getHtml() + "'" +
            ", formatted='" + getFormatted() + "'" +
            ", types='" + getTypes() + "'" +
            "}";
    }
}
