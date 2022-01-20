package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Inspection;
import com.awsptm.repository.InspectionRepository;
import com.awsptm.service.criteria.InspectionCriteria;
import com.awsptm.service.dto.InspectionDTO;
import com.awsptm.service.mapper.InspectionMapper;
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
 * Service for executing complex queries for {@link Inspection} entities in the database.
 * The main input is a {@link InspectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InspectionDTO} or a {@link Page} of {@link InspectionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InspectionQueryService extends QueryService<Inspection> {

    private final Logger log = LoggerFactory.getLogger(InspectionQueryService.class);

    private final InspectionRepository inspectionRepository;

    private final InspectionMapper inspectionMapper;

    public InspectionQueryService(InspectionRepository inspectionRepository, InspectionMapper inspectionMapper) {
        this.inspectionRepository = inspectionRepository;
        this.inspectionMapper = inspectionMapper;
    }

    /**
     * Return a {@link List} of {@link InspectionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InspectionDTO> findByCriteria(InspectionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Inspection> specification = createSpecification(criteria);
        return inspectionMapper.toDto(inspectionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InspectionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InspectionDTO> findByCriteria(InspectionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Inspection> specification = createSpecification(criteria);
        return inspectionRepository.findAll(specification, page).map(inspectionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InspectionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Inspection> specification = createSpecification(criteria);
        return inspectionRepository.count(specification);
    }

    /**
     * Function to convert {@link InspectionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Inspection> createSpecification(InspectionCriteria criteria) {
        Specification<Inspection> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Inspection_.id));
            }
            if (criteria.getPriority() != null) {
                specification = specification.and(buildSpecification(criteria.getPriority(), Inspection_.priority));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), Inspection_.created));
            }
            if (criteria.getOccupiersName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOccupiersName(), Inspection_.occupiersName));
            }
            if (criteria.getOccupiersHomePhone() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getOccupiersHomePhone(), Inspection_.occupiersHomePhone));
            }
            if (criteria.getOccupiersWorkPhone() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getOccupiersWorkPhone(), Inspection_.occupiersWorkPhone));
            }
            if (criteria.getOccupiersMobilePhone() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getOccupiersMobilePhone(), Inspection_.occupiersMobilePhone));
            }
            if (criteria.getUpdated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdated(), Inspection_.updated));
            }
            if (criteria.getStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStart(), Inspection_.start));
            }
            if (criteria.getFrequency() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFrequency(), Inspection_.frequency));
            }
            if (criteria.getJobsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getJobsId(), root -> root.join(Inspection_.jobs, JoinType.LEFT).get(Job_.id))
                    );
            }
            if (criteria.getCreatedById() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCreatedById(),
                            root -> root.join(Inspection_.createdBy, JoinType.LEFT).get(ClientUser_.id)
                        )
                    );
            }
            if (criteria.getAddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAddressId(), root -> root.join(Inspection_.address, JoinType.LEFT).get(Address_.id))
                    );
            }
        }
        return specification;
    }
}
