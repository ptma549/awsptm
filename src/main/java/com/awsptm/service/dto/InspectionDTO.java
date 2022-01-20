package com.awsptm.service.dto;

import com.awsptm.domain.enumeration.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.awsptm.domain.Inspection} entity.
 */
public class InspectionDTO implements Serializable {

    private Long id;

    private Priority priority;

    private ZonedDateTime created;

    private String occupiersName;

    private String occupiersHomePhone;

    private String occupiersWorkPhone;

    private String occupiersMobilePhone;

    @Lob
    private String work;

    @Lob
    private String accessInstructions;

    private ZonedDateTime updated;

    /**
     * when Jobs should start being scheduled from
     */
    @Schema(description = "when Jobs should start being scheduled from")
    private LocalDate start;

    /**
     * when period between inspections
     */
    @Schema(description = "when period between inspections")
    private Duration frequency;

    private JobDTO jobs;

    private ClientUserDTO createdBy;

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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public Duration getFrequency() {
        return frequency;
    }

    public void setFrequency(Duration frequency) {
        this.frequency = frequency;
    }

    public JobDTO getJobs() {
        return jobs;
    }

    public void setJobs(JobDTO jobs) {
        this.jobs = jobs;
    }

    public ClientUserDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ClientUserDTO createdBy) {
        this.createdBy = createdBy;
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
        if (!(o instanceof InspectionDTO)) {
            return false;
        }

        InspectionDTO inspectionDTO = (InspectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inspectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InspectionDTO{" +
            "id=" + getId() +
            ", priority='" + getPriority() + "'" +
            ", created='" + getCreated() + "'" +
            ", occupiersName='" + getOccupiersName() + "'" +
            ", occupiersHomePhone='" + getOccupiersHomePhone() + "'" +
            ", occupiersWorkPhone='" + getOccupiersWorkPhone() + "'" +
            ", occupiersMobilePhone='" + getOccupiersMobilePhone() + "'" +
            ", work='" + getWork() + "'" +
            ", accessInstructions='" + getAccessInstructions() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", start='" + getStart() + "'" +
            ", frequency='" + getFrequency() + "'" +
            ", jobs=" + getJobs() +
            ", createdBy=" + getCreatedBy() +
            ", address=" + getAddress() +
            "}";
    }
}
