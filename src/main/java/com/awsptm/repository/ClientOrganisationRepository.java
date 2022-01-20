package com.awsptm.repository;

import com.awsptm.domain.ClientOrganisation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ClientOrganisation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientOrganisationRepository
    extends JpaRepository<ClientOrganisation, Long>, JpaSpecificationExecutor<ClientOrganisation> {}
