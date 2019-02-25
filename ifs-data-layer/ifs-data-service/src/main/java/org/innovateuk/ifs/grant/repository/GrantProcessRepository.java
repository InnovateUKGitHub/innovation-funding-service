package org.innovateuk.ifs.grant.repository;

import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GrantProcessRepository extends PagingAndSortingRepository<GrantProcess, Long> {

    GrantProcess findOneByApplicationId(long applicationId);

    List<GrantProcess> findByPendingIsFalse();
}
