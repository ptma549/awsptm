package com.awsptm.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.awsptm.domain.Visit} entity.
 */
public class VisitDTO implements Serializable {

    private Long id;

    private ZonedDateTime arrived;

    private ZonedDateTime departed;

    @Lob
    private String description;

    @Lob
    private String actions;

    private BigDecimal labour;

    private JobDTO job;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getArrived() {
        return arrived;
    }

    public void setArrived(ZonedDateTime arrived) {
        this.arrived = arrived;
    }

    public ZonedDateTime getDeparted() {
        return departed;
    }

    public void setDeparted(ZonedDateTime departed) {
        this.departed = departed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public BigDecimal getLabour() {
        return labour;
    }

    public void setLabour(BigDecimal labour) {
        this.labour = labour;
    }

    public JobDTO getJob() {
        return job;
    }

    public void setJob(JobDTO job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitDTO)) {
            return false;
        }

        VisitDTO visitDTO = (VisitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, visitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitDTO{" +
            "id=" + getId() +
            ", arrived='" + getArrived() + "'" +
            ", departed='" + getDeparted() + "'" +
            ", description='" + getDescription() + "'" +
            ", actions='" + getActions() + "'" +
            ", labour=" + getLabour() +
            ", job=" + getJob() +
            "}";
    }
}
