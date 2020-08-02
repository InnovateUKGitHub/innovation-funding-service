package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;

@Service
public class CompetitionSetupInnovationLeadRestServiceImpl extends BaseRestService implements CompetitionSetupInnovationLeadRestService {

    private String COMPETITIONS_INNOVATION_LEAD_REST_URL = "/competition/setup";

    @Override
    public RestResult<List<UserResource>> findAvailableInnovationLeadsNotAssignedToCompetition(long competitionId) {
        return getWithRestResult(format("%s/%d/%s", COMPETITIONS_INNOVATION_LEAD_REST_URL, competitionId, "innovation-leads"), userListType());
    }

    @Override
    public RestResult<List<UserResource>> findInnovationLeadsAssignedToCompetition(long competitionId) {
        return getWithRestResult(format("%s/%d/%s", COMPETITIONS_INNOVATION_LEAD_REST_URL, competitionId, "innovation-leads/find-added"), userListType());
    }

    @Override
    public RestResult<Void> addInnovationLead(long competitionId, long innovationLeadUserId) {
        return postWithRestResult(format("%s/%d/%s/%d", COMPETITIONS_INNOVATION_LEAD_REST_URL, competitionId, "add-innovation-lead", innovationLeadUserId), Void.class);
    }

    @Override
    public RestResult<Void> removeInnovationLead(long competitionId, long innovationLeadUserId) {
        return postWithRestResult(format("%s/%d/%s/%d", COMPETITIONS_INNOVATION_LEAD_REST_URL, competitionId, "remove-innovation-lead", innovationLeadUserId), Void.class);
    }
}
