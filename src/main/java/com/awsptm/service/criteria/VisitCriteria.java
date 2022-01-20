package com.awsptm.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.awsptm.domain.Visit} entity. This class is used
 * in {@link com.awsptm.web.rest.VisitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /visits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class VisitCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter arrived;

    private ZonedDateTimeFilter departed;

    private BigDecimalFilter labour;

    private LongFilter materialsId;

    private LongFilter certificatesId;

    private LongFilter evidencesId;

    private LongFilter jobId;

    private Boolean distinct;

    public VisitCriteria() {}

    public VisitCriteria(VisitCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.arrived = other.arrived == null ? null : other.arrived.copy();
        this.departed = other.departed == null ? null : other.departed.copy();
        this.labour = other.labour == null ? null : other.labour.copy();
        this.materialsId = other.materialsId == null ? null : other.materialsId.copy();
        this.certificatesId = other.certificatesId == null ? null : other.certificatesId.copy();
        this.evidencesId = other.evidencesId == null ? null : other.evidencesId.copy();
        this.jobId = other.jobId == null ? null : other.jobId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public VisitCriteria copy() {
        return new VisitCriteria(this);
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

    public ZonedDateTimeFilter getArrived() {
        return arrived;
    }

    public ZonedDateTimeFilter arrived() {
        if (arrived == null) {
            arrived = new ZonedDateTimeFilter();
        }
        return arrived;
    }

    public void setArrived(ZonedDateTimeFilter arrived) {
        this.arrived = arrived;
    }

    public ZonedDateTimeFilter getDeparted() {
        return departed;
    }

    public ZonedDateTimeFilter departed() {
        if (departed == null) {
            departed = new ZonedDateTimeFilter();
        }
        return departed;
    }

    public void setDeparted(ZonedDateTimeFilter departed) {
        this.departed = departed;
    }

    public BigDecimalFilter getLabour() {
        return labour;
    }

    public BigDecimalFilter labour() {
        if (labour == null) {
            labour = new BigDecimalFilter();
        }
        return labour;
    }

    public void setLabour(BigDecimalFilter labour) {
        this.labour = labour;
    }

    public LongFilter getMaterialsId() {
        return materialsId;
    }

    public LongFilter materialsId() {
        if (materialsId == null) {
            materialsId = new LongFilter();
        }
        return materialsId;
    }

    public void setMaterialsId(LongFilter materialsId) {
        this.materialsId = materialsId;
    }

    public LongFilter getCertificatesId() {
        return certificatesId;
    }

    public LongFilter certificatesId() {
        if (certificatesId == null) {
            certificatesId = new LongFilter();
        }
        return certificatesId;
    }

    public void setCertificatesId(LongFilter certificatesId) {
        this.certificatesId = certificatesId;
    }

    public LongFilter getEvidencesId() {
        return evidencesId;
    }

    public LongFilter evidencesId() {
        if (evidencesId == null) {
            evidencesId = new LongFilter();
        }
        return evidencesId;
    }

    public void setEvidencesId(LongFilter evidencesId) {
        this.evidencesId = evidencesId;
    }

    public LongFilter getJobId() {
        return jobId;
    }

    public LongFilter jobId() {
        if (jobId == null) {
            jobId = new LongFilter();
        }
        return jobId;
    }

    public void setJobId(LongFilter jobId) {
        this.jobId = jobId;
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
        final VisitCriteria that = (VisitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(arrived, that.arrived) &&
            Objects.equals(departed, that.departed) &&
            Objects.equals(labour, that.labour) &&
            Objects.equals(materialsId, that.materialsId) &&
            Objects.equals(certificatesId, that.certificatesId) &&
            Objects.equals(evidencesId, that.evidencesId) &&
            Objects.equals(jobId, that.jobId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, arrived, departed, labour, materialsId, certificatesId, evidencesId, jobId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (arrived != null ? "arrived=" + arrived + ", " : "") +
            (departed != null ? "departed=" + departed + ", " : "") +
            (labour != null ? "labour=" + labour + ", " : "") +
            (materialsId != null ? "materialsId=" + materialsId + ", " : "") +
            (certificatesId != null ? "certificatesId=" + certificatesId + ", " : "") +
            (evidencesId != null ? "evidencesId=" + evidencesId + ", " : "") +
            (jobId != null ? "jobId=" + jobId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
