package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.GoogleAddress;
import com.awsptm.repository.GoogleAddressRepository;
import com.awsptm.service.criteria.GoogleAddressCriteria;
import com.awsptm.service.dto.GoogleAddressDTO;
import com.awsptm.service.mapper.GoogleAddressMapper;
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
 * Service for executing complex queries for {@link GoogleAddress} entities in the database.
 * The main input is a {@link GoogleAddressCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link GoogleAddressDTO} or a {@link Page} of {@link GoogleAddressDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GoogleAddressQueryService extends QueryService<GoogleAddress> {

    private final Logger log = LoggerFactory.getLogger(GoogleAddressQueryService.class);

    private final GoogleAddressRepository googleAddressRepository;

    private final GoogleAddressMapper googleAddressMapper;

    public GoogleAddressQueryService(GoogleAddressRepository googleAddressRepository, GoogleAddressMapper googleAddressMapper) {
        this.googleAddressRepository = googleAddressRepository;
        this.googleAddressMapper = googleAddressMapper;
    }

    /**
     * Return a {@link List} of {@link GoogleAddressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<GoogleAddressDTO> findByCriteria(GoogleAddressCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<GoogleAddress> specification = createSpecification(criteria);
        return googleAddressMapper.toDto(googleAddressRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link GoogleAddressDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<GoogleAddressDTO> findByCriteria(GoogleAddressCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<GoogleAddress> specification = createSpecification(criteria);
        return googleAddressRepository.findAll(specification, page).map(googleAddressMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GoogleAddressCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<GoogleAddress> specification = createSpecification(criteria);
        return googleAddressRepository.count(specification);
    }

    /**
     * Function to convert {@link GoogleAddressCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<GoogleAddress> createSpecification(GoogleAddressCriteria criteria) {
        Specification<GoogleAddress> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), GoogleAddress_.id));
            }
            if (criteria.getPosition() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPosition(), GoogleAddress_.position));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), GoogleAddress_.url));
            }
            if (criteria.getHtml() != null) {
                specification = specification.and(buildStringSpecification(criteria.getHtml(), GoogleAddress_.html));
            }
            if (criteria.getFormatted() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFormatted(), GoogleAddress_.formatted));
            }
            if (criteria.getTypes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTypes(), GoogleAddress_.types));
            }
            if (criteria.getAddressComponentsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAddressComponentsId(),
                            root -> root.join(GoogleAddress_.addressComponents, JoinType.LEFT).get(AddressComponent_.id)
                        )
                    );
            }
            if (criteria.getAddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAddressId(),
                            root -> root.join(GoogleAddress_.address, JoinType.LEFT).get(Address_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
