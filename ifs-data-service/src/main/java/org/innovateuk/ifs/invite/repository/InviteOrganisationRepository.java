package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InviteOrganisationRepository extends PagingAndSortingRepository<InviteOrganisation, Long> {
    List<InviteOrganisation> findByInvitesApplicationId(@Param("applicationId") Long applicationId);
    List<InviteOrganisation> findByOrganisationId(@Param("organisationId") Long organisationId);
}
