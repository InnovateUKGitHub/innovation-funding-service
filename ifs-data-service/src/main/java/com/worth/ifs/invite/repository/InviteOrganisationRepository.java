package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.InviteOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InviteOrganisationRepository extends PagingAndSortingRepository<InviteOrganisation, Long> {
}