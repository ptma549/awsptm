package com.awsptm.repository;

import com.awsptm.domain.ClientUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ClientUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientUserRepository extends JpaRepository<ClientUser, Long>, JpaSpecificationExecutor<ClientUser> {}
