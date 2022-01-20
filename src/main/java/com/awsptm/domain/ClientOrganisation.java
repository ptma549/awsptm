package com.awsptm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Allows groups of users to be defined by organisation.
 */
@Entity
@Table(name = "client_organisation")
public class ClientOrganisation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "domain")
    private String domain;

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "user", "jobs", "inspections", "client" }, allowSetters = true)
    private Set<ClientUser> clientUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientOrganisation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ClientOrganisation name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return this.domain;
    }

    public ClientOrganisation domain(String domain) {
        this.setDomain(domain);
        return this;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Set<ClientUser> getClientUsers() {
        return this.clientUsers;
    }

    public void setClientUsers(Set<ClientUser> clientUsers) {
        if (this.clientUsers != null) {
            this.clientUsers.forEach(i -> i.setClient(null));
        }
        if (clientUsers != null) {
            clientUsers.forEach(i -> i.setClient(this));
        }
        this.clientUsers = clientUsers;
    }

    public ClientOrganisation clientUsers(Set<ClientUser> clientUsers) {
        this.setClientUsers(clientUsers);
        return this;
    }

    public ClientOrganisation addClientUsers(ClientUser clientUser) {
        this.clientUsers.add(clientUser);
        clientUser.setClient(this);
        return this;
    }

    public ClientOrganisation removeClientUsers(ClientUser clientUser) {
        this.clientUsers.remove(clientUser);
        clientUser.setClient(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientOrganisation)) {
            return false;
        }
        return id != null && id.equals(((ClientOrganisation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientOrganisation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", domain='" + getDomain() + "'" +
            "}";
    }
}
