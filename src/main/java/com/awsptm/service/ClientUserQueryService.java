package com.awsptm.service;

import com.awsptm.domain.*; // for static metamodels
import com.awsptm.domain.ClientUser;
import com.awsptm.repository.ClientUserRepository;
import com.awsptm.service.criteria.ClientUserCriteria;
import com.awsptm.service.dto.ClientUserDTO;
import com.awsptm.service.mapper.ClientUserMapper;
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
 * Service for executing complex queries for {@link ClientUser} entities in the database.
 * The main input is a {@link ClientUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ClientUserDTO} or a {@link Page} of {@link ClientUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ClientUserQueryService extends QueryService<ClientUser> {

    private final Logger log = LoggerFactory.getLogger(ClientUserQueryService.class);

    private final ClientUserRepository clientUserRepository;

    private final ClientUserMapper clientUserMapper;

    public ClientUserQueryService(ClientUserRepository clientUserRepository, ClientUserMapper clientUserMapper) {
        this.clientUserRepository = clientUserRepository;
        this.clientUserMapper = clientUserMapper;
    }

    /**
     * Return a {@link List} of {@link ClientUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ClientUserDTO> findByCriteria(ClientUserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ClientUser> specification = createSpecification(criteria);
        return clientUserMapper.toDto(clientUserRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ClientUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ClientUserDTO> findByCriteria(ClientUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ClientUser> specification = createSpecification(criteria);
        return clientUserRepository.findAll(specification, page).map(clientUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ClientUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ClientUser> specification = createSpecification(criteria);
        return clientUserRepository.count(specification);
    }

    /**
     * Function to convert {@link ClientUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ClientUser> createSpecification(ClientUserCriteria criteria) {
        Specification<ClientUser> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ClientUser_.id));
            }
            if (criteria.getLandline() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLandline(), ClientUser_.landline));
            }
            if (criteria.getMobile() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMobile(), ClientUser_.mobile));
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(ClientUser_.user, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getJobsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getJobsId(), root -> root.join(ClientUser_.jobs, JoinType.LEFT).get(Job_.id))
                    );
            }
            if (criteria.getInspectionsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getInspectionsId(),
                            root -> root.join(ClientUser_.inspections, JoinType.LEFT).get(Inspection_.id)
                        )
                    );
            }
            if (criteria.getClientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getClientId(),
                            root -> root.join(ClientUser_.client, JoinType.LEFT).get(ClientOrganisation_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
