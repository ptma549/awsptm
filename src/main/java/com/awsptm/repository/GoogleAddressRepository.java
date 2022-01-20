package com.awsptm.repository;

import com.awsptm.domain.GoogleAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GoogleAddress entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GoogleAddressRepository extends JpaRepository<GoogleAddress, Long>, JpaSpecificationExecutor<GoogleAddress> {}
