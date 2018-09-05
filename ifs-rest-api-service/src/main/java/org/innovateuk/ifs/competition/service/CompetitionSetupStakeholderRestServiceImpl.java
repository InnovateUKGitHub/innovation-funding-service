package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.springframework.stereotype.Service;

/**
 * Implements {@link CompetitionSetupStakeholderRestService}
 */
@Service
public class CompetitionSetupStakeholderRestServiceImpl extends BaseRestService implements CompetitionSetupStakeholderRestService {

    private String competitionSetupStakeholderRestURL = "/competition/setup/stakeholder";

    @Override
    public RestResult<Void> inviteStakeholder(InviteUserResource inviteUserResource) {
        return postWithRestResult(competitionSetupStakeholderRestURL + "/invite", inviteUserResource, Void.class);
    }
}


