package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.ClientOrganisation;
import com.awsptm.repository.ClientOrganisationRepository;
import com.awsptm.service.criteria.ClientOrganisationCriteria;
import com.awsptm.service.dto.ClientOrganisationDTO;
import com.awsptm.service.mapper.ClientOrganisationMapper;
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
 * Service for executing complex queries for {@link ClientOrganisation} entities in the database.
 * The main input is a {@link ClientOrganisationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ClientOrganisationDTO} or a {@link Page} of {@link ClientOrganisationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientOrganisationQueryService extends QueryService<ClientOrganisation> {

    private final Logger log = LoggerFactory.getLogger(ClientOrganisationQueryService.class);

    private final ClientOrganisationRepository clientOrganisationRepository;

    private final ClientOrganisationMapper clientOrganisationMapper;

    public ClientOrganisationQueryService(
        ClientOrganisationRepository clientOrganisationRepository,
        ClientOrganisationMapper clientOrganisationMapper
    ) {
        this.clientOrganisationRepository = clientOrganisationRepository;
        this.clientOrganisationMapper = clientOrganisationMapper;
    }

    /**
     * Return a {@link List} of {@link ClientOrganisationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ClientOrganisationDTO> findByCriteria(ClientOrganisationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ClientOrganisation> specification = createSpecification(criteria);
        return clientOrganisationMapper.toDto(clientOrganisationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ClientOrganisationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientOrganisationDTO> findByCriteria(ClientOrganisationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClientOrganisation> specification = createSpecification(criteria);
        return clientOrganisationRepository.findAll(specification, page).map(clientOrganisationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClientOrganisationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ClientOrganisation> specification = createSpecification(criteria);
        return clientOrganisationRepository.count(specification);
    }

    /**
     * Function to convert {@link ClientOrganisationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClientOrganisation> createSpecification(ClientOrganisationCriteria criteria) {
        Specification<ClientOrganisation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ClientOrganisation_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ClientOrganisation_.name));
            }
            if (criteria.getDomain() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDomain(), ClientOrganisation_.domain));
            }
            if (criteria.getClientUsersId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClientUsersId(),
                            root -> root.join(ClientOrganisation_.clientUsers, JoinType.LEFT).get(ClientUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
