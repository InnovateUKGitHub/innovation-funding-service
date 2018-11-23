package org.innovateuk.ifs.grant.repository;

import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GrantProcessRepository extends PagingAndSortingRepository<GrantProcess, Long> {
    String READY_TO_SEND = "SELECT g FROM GrantProcess g WHERE g.sentSucceeded is NULL";

    @Query(READY_TO_SEND)
    List<GrantProcess> findReadyToSend();

    GrantProcess findOneByApplicationId(final Long applicationId);
}
