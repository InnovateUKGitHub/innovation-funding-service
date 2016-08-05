package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.Invite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationInviteRepository extends PagingAndSortingRepository<ApplicationInvite, Long> {

    List<ApplicationInvite> findByApplicationId(@Param("applicationId") Long applicationId);
    ApplicationInvite getByHash(@Param("hash") String hash);
}