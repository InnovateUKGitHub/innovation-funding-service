package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationInviteRepository extends PagingAndSortingRepository<ApplicationInvite, Long> {

    List<ApplicationInvite> findByApplicationId(@Param("applicationId") Long applicationId);
    ApplicationInvite getByHash(@Param("hash") String hash);
}
