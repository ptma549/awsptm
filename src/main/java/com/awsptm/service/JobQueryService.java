package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Job;
import com.awsptm.repository.JobRepository;
import com.awsptm.service.criteria.JobCriteria;
import com.awsptm.service.dto.JobDTO;
import com.awsptm.service.mapper.JobMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Job} entities in the database.
 * The main input is a {@link JobCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JobDTO} or a {@link Page} of {@link JobDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JobQueryService extends QueryService<Job> {

    private final Logger log = LoggerFactory.getLogger(JobQueryService.class);

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobQueryService(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    /**
     * Return a {@link List} of {@link JobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JobDTO> findByCriteria(JobCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Job> specification = createSpecification(criteria);
        return jobMapper.toDto(jobRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link JobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JobDTO> findByCriteria(JobCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Job> specification = createSpecification(criteria);
        return jobRepository.findAll(specification, page).map(jobMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JobCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Job> specification = createSpecification(criteria);
        return jobRepository.count(specification);
    }

    /**
     * Function to convert {@link JobCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Job> createSpecification(JobCriteria criteria) {
        Specification<Job> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Job_.id));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildSpecification(criteria.getPriority(), Job_.priority));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), Job_.created));
            }
            if (criteria.getOccupiersName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupiersName(), Job_.occupiersName));
            }
            if (criteria.getOccupiersHomePhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupiersHomePhone(), Job_.occupiersHomePhone));
            }
            if (criteria.getOccupiersWorkPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupiersWorkPhone(), Job_.occupiersWorkPhone));
            }
            if (criteria.getOccupiersMobilePhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupiersMobilePhone(), Job_.occupiersMobilePhone));
            }
            if (criteria.getClientOrderId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClientOrderId(), Job_.clientOrderId));
            }
            if (criteria.getAssignedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAssignedAt(), Job_.assignedAt));
            }
            if (criteria.getScheduled() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getScheduled(), Job_.scheduled));
            }
            if (criteria.getCompleted() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCompleted(), Job_.completed));
            }
            if (criteria.getInvoiceNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInvoiceNumber(), Job_.invoiceNumber));
            }
            if (criteria.getUpdated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdated(), Job_.updated));
            }
            if (criteria.getInspectionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getInspectionId(),
                            root -> root.join(Job_.inspections, JoinType.LEFT).get(Inspection_.id)
                        )
                    );
            }
            if (criteria.getVisitsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVisitsId(), root -> root.join(Job_.visits, JoinType.LEFT).get(Visit_.id))
                    );
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCreatedById(), root -> root.join(Job_.createdBy, JoinType.LEFT).get(ClientUser_.id))
                    );
            }
            if (criteria.getAssignedToId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAssignedToId(), root -> root.join(Job_.assignedTo, JoinType.LEFT).get(Engineer_.id))
                    );
            }
            if (criteria.getAddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAddressId(), root -> root.join(Job_.address, JoinType.LEFT).get(Address_.id))
                    );
            }
        }
        return specification;
    }
}
