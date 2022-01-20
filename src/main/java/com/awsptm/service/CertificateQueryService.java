package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.Certificate;
import com.awsptm.repository.CertificateRepository;
import com.awsptm.service.criteria.CertificateCriteria;
import com.awsptm.service.dto.CertificateDTO;
import com.awsptm.service.mapper.CertificateMapper;
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
 * Service for executing complex queries for {@link Certificate} entities in the database.
 * The main input is a {@link CertificateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CertificateDTO} or a {@link Page} of {@link CertificateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CertificateQueryService extends QueryService<Certificate> {

    private final Logger log = LoggerFactory.getLogger(CertificateQueryService.class);

    private final CertificateRepository certificateRepository;

    private final CertificateMapper certificateMapper;

    public CertificateQueryService(CertificateRepository certificateRepository, CertificateMapper certificateMapper) {
        this.certificateRepository = certificateRepository;
        this.certificateMapper = certificateMapper;
    }

    /**
     * Return a {@link List} of {@link CertificateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CertificateDTO> findByCriteria(CertificateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateMapper.toDto(certificateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CertificateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CertificateDTO> findByCriteria(CertificateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.findAll(specification, page).map(certificateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CertificateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Certificate> specification = createSpecification(criteria);
        return certificateRepository.count(specification);
    }

    /**
     * Function to convert {@link CertificateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Certificate> createSpecification(CertificateCriteria criteria) {
        Specification<Certificate> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Certificate_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Certificate_.name));
            }
            if (criteria.getVisitId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getVisitId(), root -> root.join(Certificate_.visit, JoinType.LEFT).get(Visit_.id))
                    );
            }
        }
        return specification;
    }
}
