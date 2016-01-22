package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.Invite;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InviteRepository extends PagingAndSortingRepository<Invite, Long> {

    List<Invite> findByApplicationId(@Param("applicationId") Long applicationId);
}