package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;

import java.util.List;
import java.util.Set;

public interface ApplicationInviteRepository extends InviteRepository<ApplicationInvite> {

    List<ApplicationInvite> findByInviteOrganisationOrganisationNameLikeAndStatusIn(String organisationName, Set<InviteStatus> status);

    List<ApplicationInvite> findByApplicationId(Long applicationId);
}
