package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;

/**
 * Interface for CRUD operations on Stakeholder related data.
 */
public interface CompetitionSetupStakeholderRestService {

    RestResult<Void> inviteStakeholder(InviteUserResource inviteUserResource, long competitionId);
}

