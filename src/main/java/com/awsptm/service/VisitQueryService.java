package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Visit;
import com.awsptm.repository.VisitRepository;
import com.awsptm.service.criteria.VisitCriteria;
import com.awsptm.service.dto.VisitDTO;
import com.awsptm.service.mapper.VisitMapper;
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
 * Service for executing complex queries for {@link Visit} entities in the database.
 * The main input is a {@link VisitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link VisitDTO} or a {@link Page} of {@link VisitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VisitQueryService extends QueryService<Visit> {

    private final Logger log = LoggerFactory.getLogger(VisitQueryService.class);

    private final VisitRepository visitRepository;

    private final VisitMapper visitMapper;

    public VisitQueryService(VisitRepository visitRepository, VisitMapper visitMapper) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
    }

    /**
     * Return a {@link List} of {@link VisitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<VisitDTO> findByCriteria(VisitCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitMapper.toDto(visitRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link VisitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VisitDTO> findByCriteria(VisitCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitRepository.findAll(specification, page).map(visitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VisitCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Visit> specification = createSpecification(criteria);
        return visitRepository.count(specification);
    }

    /**
     * Function to convert {@link VisitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Visit> createSpecification(VisitCriteria criteria) {
        Specification<Visit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Visit_.id));
            }
            if (criteria.getArrived() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getArrived(), Visit_.arrived));
            }
            if (criteria.getDeparted() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDeparted(), Visit_.departed));
            }
            if (criteria.getLabour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLabour(), Visit_.labour));
            }
            if (criteria.getMaterialsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getMaterialsId(), root -> root.join(Visit_.materials, JoinType.LEFT).get(Material_.id))
                    );
            }
            if (criteria.getCertificatesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCertificatesId(),
                            root -> root.join(Visit_.certificates, JoinType.LEFT).get(Certificate_.id)
                        )
                    );
            }
            if (criteria.getEvidencesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEvidencesId(), root -> root.join(Visit_.evidences, JoinType.LEFT).get(Evidence_.id))
                    );
            }
            if (criteria.getJobId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getJobId(), root -> root.join(Visit_.job, JoinType.LEFT).get(Job_.id)));
            }
        }
        return specification;
    }
}
