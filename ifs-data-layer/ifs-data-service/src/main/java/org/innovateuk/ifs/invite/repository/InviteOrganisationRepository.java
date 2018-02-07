package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface InviteOrganisationRepository extends PagingAndSortingRepository<InviteOrganisation, Long> {

    InviteOrganisation findOneByOrganisationIdAndInvitesApplicationId(long organisationId, long applicationId);

    List<InviteOrganisation> findDistinctByInvitesApplicationId(Long applicationId);

    Optional<InviteOrganisation> findFirstByOrganisationIdAndInvitesApplicationId(Long organisationId, Long applicationId);
}
