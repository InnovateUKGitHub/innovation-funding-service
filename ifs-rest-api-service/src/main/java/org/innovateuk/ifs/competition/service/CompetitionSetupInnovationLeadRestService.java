package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public interface CompetitionSetupInnovationLeadRestService {

    RestResult<List<UserResource>> findAvailableInnovationLeadsNotAssignedToCompetition(long competitionId);

    RestResult<List<UserResource>> findInnovationLeadsAssignedToCompetition(long competitionId);

    RestResult<Void> addInnovationLead(long competitionId, long innovationLeadUserId);

    RestResult<Void> removeInnovationLead(long competitionId, long innovationLeadUserId);
}
