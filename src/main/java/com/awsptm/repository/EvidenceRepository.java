package com.awsptm.repository;

import com.awsptm.domain.Evidence;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Evidence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long>, JpaSpecificationExecutor<Evidence> {}
