package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.milestoneResourceListType;

/**
 * MilestoneRestServiceImpl is a utility for CRUD operations on {@link MilestoneResource}.
 * This class connects to the { com.worth.ifs.competition.controller.MilestoneController}
 * through a REST call.
 */
@Service
public class MilestoneRestServiceImpl extends BaseRestService implements MilestoneRestService {

    private String milestonesRestURL = "/milestone";

    @Override
    public RestResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long competitionId) {
        return getWithRestResult(milestonesRestURL + "/" + competitionId, milestoneResourceListType());
    }

    @Override
    public RestResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId) {
        return getWithRestResult(milestonesRestURL + "/" + competitionId + "/getByType?type=" + type, MilestoneResource.class);
    }

    @Override
    public RestResult<Void> updateMilestones(List<MilestoneResource> milestones, Long competitionId) {
        return putWithRestResult(milestonesRestURL + "/" + competitionId, milestones, Void.class);
    }

    @Override
    public RestResult<Void> updateMilestone(MilestoneResource milestone) {
        return putWithRestResult(milestonesRestURL + "/", milestone, Void.class);
    }

    @Override
    public RestResult<MilestoneResource> create(MilestoneType type, Long competitionId) {
        return postWithRestResult(milestonesRestURL + "/" + competitionId, type, MilestoneResource.class);
    }
}
