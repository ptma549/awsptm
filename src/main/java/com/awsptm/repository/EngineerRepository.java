package com.awsptm.repository;

import com.awsptm.domain.Engineer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Engineer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EngineerRepository extends JpaRepository<Engineer, Long>, JpaSpecificationExecutor<Engineer> {}
