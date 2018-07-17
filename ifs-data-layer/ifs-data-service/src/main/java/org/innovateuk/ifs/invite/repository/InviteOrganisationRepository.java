package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InviteOrganisationRepository extends JpaRepository<InviteOrganisation, Long> {

    InviteOrganisation findOneByOrganisationIdAndInvitesApplicationId(long organisationId, long applicationId);

    List<InviteOrganisation> findDistinctByInvitesApplicationId(Long applicationId);

    Optional<InviteOrganisation> findFirstByOrganisationIdAndInvitesApplicationId(Long organisationId, Long applicationId);

    Optional<InviteOrganisation> findFirstByOrganisationIdAndInvitesUserId(Long organisationId, Long userId);
}
