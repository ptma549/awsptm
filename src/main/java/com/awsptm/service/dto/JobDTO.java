package com.awsptm.service.dto;

import com.awsptm.domain.enumeration.Priority;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.awsptm.domain.Job} entity.
 */
public class JobDTO implements Serializable {

    private Long id;

    private Priority priority;

    private ZonedDateTime created;

    private String occupiersName;

    private String occupiersHomePhone;

    private String occupiersWorkPhone;

    private String occupiersMobilePhone;

    private String clientOrderId;

    private ZonedDateTime assignedAt;

    private ZonedDateTime scheduled;

    private ZonedDateTime completed;

    private String invoiceNumber;

    @Lob
    private String fault;

    @Lob
    private String accessInstructions;

    private ZonedDateTime updated;

    private ClientUserDTO createdBy;

    private EngineerDTO assignedTo;

    private AddressDTO address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getOccupiersName() {
        return occupiersName;
    }

    public void setOccupiersName(String occupiersName) {
        this.occupiersName = occupiersName;
    }

    public String getOccupiersHomePhone() {
        return occupiersHomePhone;
    }

    public void setOccupiersHomePhone(String occupiersHomePhone) {
        this.occupiersHomePhone = occupiersHomePhone;
    }

    public String getOccupiersWorkPhone() {
        return occupiersWorkPhone;
    }

    public void setOccupiersWorkPhone(String occupiersWorkPhone) {
        this.occupiersWorkPhone = occupiersWorkPhone;
    }

    public String getOccupiersMobilePhone() {
        return occupiersMobilePhone;
    }

    public void setOccupiersMobilePhone(String occupiersMobilePhone) {
        this.occupiersMobilePhone = occupiersMobilePhone;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(ZonedDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public ZonedDateTime getScheduled() {
        return scheduled;
    }

    public void setScheduled(ZonedDateTime scheduled) {
        this.scheduled = scheduled;
    }

    public ZonedDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(ZonedDateTime completed) {
        this.completed = completed;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getFault() {
        return fault;
    }

    public void setFault(String fault) {
        this.fault = fault;
    }

    public String getAccessInstructions() {
        return accessInstructions;
    }

    public void setAccessInstructions(String accessInstructions) {
        this.accessInstructions = accessInstructions;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public ClientUserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ClientUserDTO createdBy) {
        this.createdBy = createdBy;
    }

    public EngineerDTO getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(EngineerDTO assignedTo) {
        this.assignedTo = assignedTo;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobDTO)) {
            return false;
        }

        JobDTO jobDTO = (JobDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, jobDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobDTO{" +
            "id=" + getId() +
            ", priority='" + getPriority() + "'" +
            ", created='" + getCreated() + "'" +
            ", occupiersName='" + getOccupiersName() + "'" +
            ", occupiersHomePhone='" + getOccupiersHomePhone() + "'" +
            ", occupiersWorkPhone='" + getOccupiersWorkPhone() + "'" +
            ", occupiersMobilePhone='" + getOccupiersMobilePhone() + "'" +
            ", clientOrderId='" + getClientOrderId() + "'" +
            ", assignedAt='" + getAssignedAt() + "'" +
            ", scheduled='" + getScheduled() + "'" +
            ", completed='" + getCompleted() + "'" +
            ", invoiceNumber='" + getInvoiceNumber() + "'" +
            ", fault='" + getFault() + "'" +
            ", accessInstructions='" + getAccessInstructions() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", assignedTo=" + getAssignedTo() +
            ", address=" + getAddress() +
            "}";
    }
}
