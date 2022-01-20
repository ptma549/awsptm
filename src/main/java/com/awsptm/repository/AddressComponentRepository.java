package com.awsptm.repository;

import com.awsptm.domain.AddressComponent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AddressComponent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressComponentRepository extends JpaRepository<AddressComponent, Long>, JpaSpecificationExecutor<AddressComponent> {}
