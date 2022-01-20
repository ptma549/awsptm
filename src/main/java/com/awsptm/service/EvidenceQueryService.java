package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Evidence;
import com.awsptm.repository.EvidenceRepository;
import com.awsptm.service.criteria.EvidenceCriteria;
import com.awsptm.service.dto.EvidenceDTO;
import com.awsptm.service.mapper.EvidenceMapper;
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
 * Service for executing complex queries for {@link Evidence} entities in the database.
 * The main input is a {@link EvidenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EvidenceDTO} or a {@link Page} of {@link EvidenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EvidenceQueryService extends QueryService<Evidence> {

    private final Logger log = LoggerFactory.getLogger(EvidenceQueryService.class);

    private final EvidenceRepository evidenceRepository;

    private final EvidenceMapper evidenceMapper;

    public EvidenceQueryService(EvidenceRepository evidenceRepository, EvidenceMapper evidenceMapper) {
        this.evidenceRepository = evidenceRepository;
        this.evidenceMapper = evidenceMapper;
    }

    /**
     * Return a {@link List} of {@link EvidenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EvidenceDTO> findByCriteria(EvidenceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Evidence> specification = createSpecification(criteria);
        return evidenceMapper.toDto(evidenceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EvidenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EvidenceDTO> findByCriteria(EvidenceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Evidence> specification = createSpecification(criteria);
        return evidenceRepository.findAll(specification, page).map(evidenceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EvidenceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Evidence> specification = createSpecification(criteria);
        return evidenceRepository.count(specification);
    }

    /**
     * Function to convert {@link EvidenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Evidence> createSpecification(EvidenceCriteria criteria) {
        Specification<Evidence> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Evidence_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Evidence_.name));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Evidence_.url));
            }
            if (criteria.getVisitId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVisitId(), root -> root.join(Evidence_.visit, JoinType.LEFT).get(Visit_.id))
                    );
            }
        }
        return specification;
    }
}
