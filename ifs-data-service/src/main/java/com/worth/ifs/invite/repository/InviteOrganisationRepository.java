package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.InviteOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InviteOrganisationRepository extends PagingAndSortingRepository<InviteOrganisation, Long> {
    List<InviteOrganisation> findByInvitesApplicationId(@Param("applicationId") Long applicationId);
}