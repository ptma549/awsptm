package com.awsptm.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.awsptm.domain.ClientOrganisation} entity. This class is used
 * in {@link com.awsptm.web.rest.ClientOrganisationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /client-organisations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClientOrganisationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter domain;

    private LongFilter clientUsersId;

    private Boolean distinct;

    public ClientOrganisationCriteria() {}

    public ClientOrganisationCriteria(ClientOrganisationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.domain = other.domain == null ? null : other.domain.copy();
        this.clientUsersId = other.clientUsersId == null ? null : other.clientUsersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClientOrganisationCriteria copy() {
        return new ClientOrganisationCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDomain() {
        return domain;
    }

    public StringFilter domain() {
        if (domain == null) {
            domain = new StringFilter();
        }
        return domain;
    }

    public void setDomain(StringFilter domain) {
        this.domain = domain;
    }

    public LongFilter getClientUsersId() {
        return clientUsersId;
    }

    public LongFilter clientUsersId() {
        if (clientUsersId == null) {
            clientUsersId = new LongFilter();
        }
        return clientUsersId;
    }

    public void setClientUsersId(LongFilter clientUsersId) {
        this.clientUsersId = clientUsersId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ClientOrganisationCriteria that = (ClientOrganisationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(domain, that.domain) &&
            Objects.equals(clientUsersId, that.clientUsersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, domain, clientUsersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientOrganisationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (domain != null ? "domain=" + domain + ", " : "") +
            (clientUsersId != null ? "clientUsersId=" + clientUsersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
