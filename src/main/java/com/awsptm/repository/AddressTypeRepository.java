package com.awsptm.repository;

import com.awsptm.domain.AddressType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the AddressType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressTypeRepository extends JpaRepository<AddressType, Long>, JpaSpecificationExecutor<AddressType> {}
