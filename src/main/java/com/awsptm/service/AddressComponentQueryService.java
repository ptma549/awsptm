package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.AddressComponent;
import com.awsptm.repository.AddressComponentRepository;
import com.awsptm.service.criteria.AddressComponentCriteria;
import com.awsptm.service.dto.AddressComponentDTO;
import com.awsptm.service.mapper.AddressComponentMapper;
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
 * Service for executing complex queries for {@link AddressComponent} entities in the database.
 * The main input is a {@link AddressComponentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AddressComponentDTO} or a {@link Page} of {@link AddressComponentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AddressComponentQueryService extends QueryService<AddressComponent> {

    private final Logger log = LoggerFactory.getLogger(AddressComponentQueryService.class);

    private final AddressComponentRepository addressComponentRepository;

    private final AddressComponentMapper addressComponentMapper;

    public AddressComponentQueryService(
        AddressComponentRepository addressComponentRepository,
        AddressComponentMapper addressComponentMapper
    ) {
        this.addressComponentRepository = addressComponentRepository;
        this.addressComponentMapper = addressComponentMapper;
    }

    /**
     * Return a {@link List} of {@link AddressComponentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AddressComponentDTO> findByCriteria(AddressComponentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AddressComponent> specification = createSpecification(criteria);
        return addressComponentMapper.toDto(addressComponentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AddressComponentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AddressComponentDTO> findByCriteria(AddressComponentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AddressComponent> specification = createSpecification(criteria);
        return addressComponentRepository.findAll(specification, page).map(addressComponentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AddressComponentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AddressComponent> specification = createSpecification(criteria);
        return addressComponentRepository.count(specification);
    }

    /**
     * Function to convert {@link AddressComponentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AddressComponent> createSpecification(AddressComponentCriteria criteria) {
        Specification<AddressComponent> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AddressComponent_.id));
            }
            if (criteria.getLongName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLongName(), AddressComponent_.longName));
            }
            if (criteria.getShortName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortName(), AddressComponent_.shortName));
            }
            if (criteria.getAddressId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAddressId(),
                            root -> root.join(AddressComponent_.address, JoinType.LEFT).get(GoogleAddress_.id)
                        )
                    );
            }
            if (criteria.getTypeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTypeId(),
                            root -> root.join(AddressComponent_.type, JoinType.LEFT).get(AddressType_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
