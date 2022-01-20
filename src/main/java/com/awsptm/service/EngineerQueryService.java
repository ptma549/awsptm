package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Engineer;
import com.awsptm.repository.EngineerRepository;
import com.awsptm.service.criteria.EngineerCriteria;
import com.awsptm.service.dto.EngineerDTO;
import com.awsptm.service.mapper.EngineerMapper;
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
 * Service for executing complex queries for {@link Engineer} entities in the database.
 * The main input is a {@link EngineerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EngineerDTO} or a {@link Page} of {@link EngineerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EngineerQueryService extends QueryService<Engineer> {

    private final Logger log = LoggerFactory.getLogger(EngineerQueryService.class);

    private final EngineerRepository engineerRepository;

    private final EngineerMapper engineerMapper;

    public EngineerQueryService(EngineerRepository engineerRepository, EngineerMapper engineerMapper) {
        this.engineerRepository = engineerRepository;
        this.engineerMapper = engineerMapper;
    }

    /**
     * Return a {@link List} of {@link EngineerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EngineerDTO> findByCriteria(EngineerCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Engineer> specification = createSpecification(criteria);
        return engineerMapper.toDto(engineerRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link EngineerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EngineerDTO> findByCriteria(EngineerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Engineer> specification = createSpecification(criteria);
        return engineerRepository.findAll(specification, page).map(engineerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EngineerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Engineer> specification = createSpecification(criteria);
        return engineerRepository.count(specification);
    }

    /**
     * Function to convert {@link EngineerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Engineer> createSpecification(EngineerCriteria criteria) {
        Specification<Engineer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Engineer_.id));
            }
            if (criteria.getFirstname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstname(), Engineer_.firstname));
            }
            if (criteria.getLastname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastname(), Engineer_.lastname));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Engineer_.email));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(Engineer_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getJobsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getJobsId(), root -> root.join(Engineer_.jobs, JoinType.LEFT).get(Job_.id))
                    );
            }
        }
        return specification;
    }
}
