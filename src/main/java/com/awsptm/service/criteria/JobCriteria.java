package com.awsptm.service.criteria;

import com.awsptm.domain.enumeration.Priority;
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
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.awsptm.domain.Job} entity. This class is used
 * in {@link com.awsptm.web.rest.JobResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /jobs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class JobCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Priority
     */
    public static class PriorityFilter extends Filter<Priority> {

        public PriorityFilter() {}

        public PriorityFilter(PriorityFilter filter) {
            super(filter);
        }

        @Override
        public PriorityFilter copy() {
            return new PriorityFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private PriorityFilter priority;

    private ZonedDateTimeFilter created;

    private StringFilter occupiersName;

    private StringFilter occupiersHomePhone;

    private StringFilter occupiersWorkPhone;

    private StringFilter occupiersMobilePhone;

    private StringFilter clientOrderId;

    private ZonedDateTimeFilter assignedAt;

    private ZonedDateTimeFilter scheduled;

    private ZonedDateTimeFilter completed;

    private StringFilter invoiceNumber;

    private ZonedDateTimeFilter updated;

    private LongFilter inspectionId;

    private LongFilter visitsId;

    private LongFilter createdById;

    private LongFilter assignedToId;

    private LongFilter addressId;

    private Boolean distinct;

    public JobCriteria() {}

    public JobCriteria(JobCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.priority = other.priority == null ? null : other.priority.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.occupiersName = other.occupiersName == null ? null : other.occupiersName.copy();
        this.occupiersHomePhone = other.occupiersHomePhone == null ? null : other.occupiersHomePhone.copy();
        this.occupiersWorkPhone = other.occupiersWorkPhone == null ? null : other.occupiersWorkPhone.copy();
        this.occupiersMobilePhone = other.occupiersMobilePhone == null ? null : other.occupiersMobilePhone.copy();
        this.clientOrderId = other.clientOrderId == null ? null : other.clientOrderId.copy();
        this.assignedAt = other.assignedAt == null ? null : other.assignedAt.copy();
        this.scheduled = other.scheduled == null ? null : other.scheduled.copy();
        this.completed = other.completed == null ? null : other.completed.copy();
        this.invoiceNumber = other.invoiceNumber == null ? null : other.invoiceNumber.copy();
        this.updated = other.updated == null ? null : other.updated.copy();
        this.inspectionId = other.inspectionId == null ? null : other.inspectionId.copy();
        this.visitsId = other.visitsId == null ? null : other.visitsId.copy();
        this.createdById = other.createdById == null ? null : other.createdById.copy();
        this.assignedToId = other.assignedToId == null ? null : other.assignedToId.copy();
        this.addressId = other.addressId == null ? null : other.addressId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public JobCriteria copy() {
        return new JobCriteria(this);
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

    public PriorityFilter getPriority() {
        return priority;
    }

    public PriorityFilter priority() {
        if (priority == null) {
            priority = new PriorityFilter();
        }
        return priority;
    }

    public void setPriority(PriorityFilter priority) {
        this.priority = priority;
    }

    public ZonedDateTimeFilter getCreated() {
        return created;
    }

    public ZonedDateTimeFilter created() {
        if (created == null) {
            created = new ZonedDateTimeFilter();
        }
        return created;
    }

    public void setCreated(ZonedDateTimeFilter created) {
        this.created = created;
    }

    public StringFilter getOccupiersName() {
        return occupiersName;
    }

    public StringFilter occupiersName() {
        if (occupiersName == null) {
            occupiersName = new StringFilter();
        }
        return occupiersName;
    }

    public void setOccupiersName(StringFilter occupiersName) {
        this.occupiersName = occupiersName;
    }

    public StringFilter getOccupiersHomePhone() {
        return occupiersHomePhone;
    }

    public StringFilter occupiersHomePhone() {
        if (occupiersHomePhone == null) {
            occupiersHomePhone = new StringFilter();
        }
        return occupiersHomePhone;
    }

    public void setOccupiersHomePhone(StringFilter occupiersHomePhone) {
        this.occupiersHomePhone = occupiersHomePhone;
    }

    public StringFilter getOccupiersWorkPhone() {
        return occupiersWorkPhone;
    }

    public StringFilter occupiersWorkPhone() {
        if (occupiersWorkPhone == null) {
            occupiersWorkPhone = new StringFilter();
        }
        return occupiersWorkPhone;
    }

    public void setOccupiersWorkPhone(StringFilter occupiersWorkPhone) {
        this.occupiersWorkPhone = occupiersWorkPhone;
    }

    public StringFilter getOccupiersMobilePhone() {
        return occupiersMobilePhone;
    }

    public StringFilter occupiersMobilePhone() {
        if (occupiersMobilePhone == null) {
            occupiersMobilePhone = new StringFilter();
        }
        return occupiersMobilePhone;
    }

    public void setOccupiersMobilePhone(StringFilter occupiersMobilePhone) {
        this.occupiersMobilePhone = occupiersMobilePhone;
    }

    public StringFilter getClientOrderId() {
        return clientOrderId;
    }

    public StringFilter clientOrderId() {
        if (clientOrderId == null) {
            clientOrderId = new StringFilter();
        }
        return clientOrderId;
    }

    public void setClientOrderId(StringFilter clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public ZonedDateTimeFilter getAssignedAt() {
        return assignedAt;
    }

    public ZonedDateTimeFilter assignedAt() {
        if (assignedAt == null) {
            assignedAt = new ZonedDateTimeFilter();
        }
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTimeFilter assignedAt) {
        this.assignedAt = assignedAt;
    }

    public ZonedDateTimeFilter getScheduled() {
        return scheduled;
    }

    public ZonedDateTimeFilter scheduled() {
        if (scheduled == null) {
            scheduled = new ZonedDateTimeFilter();
        }
        return scheduled;
    }

    public void setScheduled(ZonedDateTimeFilter scheduled) {
        this.scheduled = scheduled;
    }

    public ZonedDateTimeFilter getCompleted() {
        return completed;
    }

    public ZonedDateTimeFilter completed() {
        if (completed == null) {
            completed = new ZonedDateTimeFilter();
        }
        return completed;
    }

    public void setCompleted(ZonedDateTimeFilter completed) {
        this.completed = completed;
    }

    public StringFilter getInvoiceNumber() {
        return invoiceNumber;
    }

    public StringFilter invoiceNumber() {
        if (invoiceNumber == null) {
            invoiceNumber = new StringFilter();
        }
        return invoiceNumber;
    }

    public void setInvoiceNumber(StringFilter invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public ZonedDateTimeFilter getUpdated() {
        return updated;
    }

    public ZonedDateTimeFilter updated() {
        if (updated == null) {
            updated = new ZonedDateTimeFilter();
        }
        return updated;
    }

    public void setUpdated(ZonedDateTimeFilter updated) {
        this.updated = updated;
    }

    public LongFilter getInspectionId() {
        return inspectionId;
    }

    public LongFilter inspectionId() {
        if (inspectionId == null) {
            inspectionId = new LongFilter();
        }
        return inspectionId;
    }

    public void setInspectionId(LongFilter inspectionId) {
        this.inspectionId = inspectionId;
    }

    public LongFilter getVisitsId() {
        return visitsId;
    }

    public LongFilter visitsId() {
        if (visitsId == null) {
            visitsId = new LongFilter();
        }
        return visitsId;
    }

    public void setVisitsId(LongFilter visitsId) {
        this.visitsId = visitsId;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public LongFilter createdById() {
        if (createdById == null) {
            createdById = new LongFilter();
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getAssignedToId() {
        return assignedToId;
    }

    public LongFilter assignedToId() {
        if (assignedToId == null) {
            assignedToId = new LongFilter();
        }
        return assignedToId;
    }

    public void setAssignedToId(LongFilter assignedToId) {
        this.assignedToId = assignedToId;
    }

    public LongFilter getAddressId() {
        return addressId;
    }

    public LongFilter addressId() {
        if (addressId == null) {
            addressId = new LongFilter();
        }
        return addressId;
    }

    public void setAddressId(LongFilter addressId) {
        this.addressId = addressId;
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
        final JobCriteria that = (JobCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(priority, that.priority) &&
            Objects.equals(created, that.created) &&
            Objects.equals(occupiersName, that.occupiersName) &&
            Objects.equals(occupiersHomePhone, that.occupiersHomePhone) &&
            Objects.equals(occupiersWorkPhone, that.occupiersWorkPhone) &&
            Objects.equals(occupiersMobilePhone, that.occupiersMobilePhone) &&
            Objects.equals(clientOrderId, that.clientOrderId) &&
            Objects.equals(assignedAt, that.assignedAt) &&
            Objects.equals(scheduled, that.scheduled) &&
            Objects.equals(completed, that.completed) &&
            Objects.equals(invoiceNumber, that.invoiceNumber) &&
            Objects.equals(updated, that.updated) &&
            Objects.equals(inspectionId, that.inspectionId) &&
            Objects.equals(visitsId, that.visitsId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(assignedToId, that.assignedToId) &&
            Objects.equals(addressId, that.addressId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            priority,
            created,
            occupiersName,
            occupiersHomePhone,
            occupiersWorkPhone,
            occupiersMobilePhone,
            clientOrderId,
            assignedAt,
            scheduled,
            completed,
            invoiceNumber,
            updated,
            inspectionId,
            visitsId,
            createdById,
            assignedToId,
            addressId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (priority != null ? "priority=" + priority + ", " : "") +
            (created != null ? "created=" + created + ", " : "") +
            (occupiersName != null ? "occupiersName=" + occupiersName + ", " : "") +
            (occupiersHomePhone != null ? "occupiersHomePhone=" + occupiersHomePhone + ", " : "") +
            (occupiersWorkPhone != null ? "occupiersWorkPhone=" + occupiersWorkPhone + ", " : "") +
            (occupiersMobilePhone != null ? "occupiersMobilePhone=" + occupiersMobilePhone + ", " : "") +
            (clientOrderId != null ? "clientOrderId=" + clientOrderId + ", " : "") +
            (assignedAt != null ? "assignedAt=" + assignedAt + ", " : "") +
            (scheduled != null ? "scheduled=" + scheduled + ", " : "") +
            (completed != null ? "completed=" + completed + ", " : "") +
            (invoiceNumber != null ? "invoiceNumber=" + invoiceNumber + ", " : "") +
            (updated != null ? "updated=" + updated + ", " : "") +
            (inspectionId != null ? "inspectionId=" + inspectionId + ", " : "") +
            (visitsId != null ? "visitsId=" + visitsId + ", " : "") +
            (createdById != null ? "createdById=" + createdById + ", " : "") +
            (assignedToId != null ? "assignedToId=" + assignedToId + ", " : "") +
            (addressId != null ? "addressId=" + addressId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
