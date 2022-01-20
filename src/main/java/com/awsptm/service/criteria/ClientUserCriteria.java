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
 * Criteria class for the {@link com.awsptm.domain.ClientUser} entity. This class is used
 * in {@link com.awsptm.web.rest.ClientUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /client-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClientUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter landline;

    private StringFilter mobile;

    private LongFilter userId;

    private LongFilter jobsId;

    private LongFilter inspectionsId;

    private LongFilter clientId;

    private Boolean distinct;

    public ClientUserCriteria() {}

    public ClientUserCriteria(ClientUserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.landline = other.landline == null ? null : other.landline.copy();
        this.mobile = other.mobile == null ? null : other.mobile.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.jobsId = other.jobsId == null ? null : other.jobsId.copy();
        this.inspectionsId = other.inspectionsId == null ? null : other.inspectionsId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClientUserCriteria copy() {
        return new ClientUserCriteria(this);
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

    public StringFilter getLandline() {
        return landline;
    }

    public StringFilter landline() {
        if (landline == null) {
            landline = new StringFilter();
        }
        return landline;
    }

    public void setLandline(StringFilter landline) {
        this.landline = landline;
    }

    public StringFilter getMobile() {
        return mobile;
    }

    public StringFilter mobile() {
        if (mobile == null) {
            mobile = new StringFilter();
        }
        return mobile;
    }

    public void setMobile(StringFilter mobile) {
        this.mobile = mobile;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getJobsId() {
        return jobsId;
    }

    public LongFilter jobsId() {
        if (jobsId == null) {
            jobsId = new LongFilter();
        }
        return jobsId;
    }

    public void setJobsId(LongFilter jobsId) {
        this.jobsId = jobsId;
    }

    public LongFilter getInspectionsId() {
        return inspectionsId;
    }

    public LongFilter inspectionsId() {
        if (inspectionsId == null) {
            inspectionsId = new LongFilter();
        }
        return inspectionsId;
    }

    public void setInspectionsId(LongFilter inspectionsId) {
        this.inspectionsId = inspectionsId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public LongFilter clientId() {
        if (clientId == null) {
            clientId = new LongFilter();
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
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
        final ClientUserCriteria that = (ClientUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(landline, that.landline) &&
            Objects.equals(mobile, that.mobile) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(jobsId, that.jobsId) &&
            Objects.equals(inspectionsId, that.inspectionsId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, landline, mobile, userId, jobsId, inspectionsId, clientId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientUserCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (landline != null ? "landline=" + landline + ", " : "") +
            (mobile != null ? "mobile=" + mobile + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (jobsId != null ? "jobsId=" + jobsId + ", " : "") +
            (inspectionsId != null ? "inspectionsId=" + inspectionsId + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
