package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Transactional and secured service providing operations around stakeholders.
 */
public interface CompetitionSetupStakeholderService {

    @SecuredBySpring(value = "SAVE_STAKEHOLDER_INVITE", description = "Only comp admin, project finance or IFS admin can save a stakeholder invite")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
    ServiceResult<Void> inviteStakeholder(UserResource invitedUser, long competitionId);
}

