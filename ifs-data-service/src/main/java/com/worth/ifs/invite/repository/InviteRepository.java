package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.Invite;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InviteRepository extends PagingAndSortingRepository<Invite, Long> {
}