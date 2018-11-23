package org.innovateuk.ifs.grant.repository;

import org.innovateuk.ifs.grant.domain.GrantStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GrantStatusRepository extends PagingAndSortingRepository<GrantStatus, Long> {
    String READY_TO_SEND = "SELECT grant FROM GrantStatus grant WHERE grant.sentSucceeded is NULL";

    @Query(READY_TO_SEND)
    List<GrantStatus> findReadyToSend();

    GrantStatus findOneByApplicationId(final Long applicationId);
}
