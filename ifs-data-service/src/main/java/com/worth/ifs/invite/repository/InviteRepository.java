package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.Invite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InviteRepository extends PagingAndSortingRepository<Invite, Long> {

    List<Invite> findByApplicationId(@Param("applicationId") Long applicationId);
    Optional<Invite> getByHash(@Param("hash") String hash);
}