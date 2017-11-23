package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;

/**
 * MilestoneRestServiceImpl is a utility for CRUD operations on {@link MilestoneResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.MilestoneController}
 * through a REST call.
 */
@Service
public class MilestoneRestServiceImpl extends BaseRestService implements MilestoneRestService {

    private String milestonesRestURL = "/milestone";

    @Override
    public RestResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(Long competitionId) {
        return getWithRestResultAnonymous(milestonesRestURL + "/" + competitionId + "/public", milestoneResourceListType());
    }

    @Override
    public RestResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long competitionId) {
        return getWithRestResult(milestonesRestURL + "/" + competitionId, milestoneResourceListType());
    }

    @Override
    public RestResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId) {
        return getWithRestResult(milestonesRestURL + "/" + competitionId + "/getByType?type=" + type, MilestoneResource.class);
    }

    @Override
    public RestResult<Void> updateMilestones(List<MilestoneResource> milestones) {
        return putWithRestResult(milestonesRestURL + "/many", milestones, Void.class);
    }

    @Override
    public RestResult<Void> updateMilestone(MilestoneResource milestone) {
        return putWithRestResult(milestonesRestURL + "/", milestone, Void.class);
    }

    @Override
    public RestResult<MilestoneResource> create(MilestoneType type, Long competitionId) {
        return postWithRestResult(milestonesRestURL + "/" + competitionId + "?type=" + type, MilestoneResource.class);
    }

    @Override
    public RestResult<Void> resetMilestone(MilestoneResource milestone) {
        milestone.setDate(null);
        return putWithRestResult(milestonesRestURL + "/", milestone, Void.class);
    }
}
