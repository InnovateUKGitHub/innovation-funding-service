package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link MilestoneResource} related data,
 * through the RestService {@link MilestoneRestService}.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService{

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public List<MilestoneResource> getAllPublicMilestonesByCompetitionId(Long competitionId) {
        return milestoneRestService.getAllPublicMilestonesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<MilestoneResource> getAllMilestonesByCompetitionId(Long competitionId) {
        return milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public MilestoneResource getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId) {
        return milestoneRestService.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones) {
       return milestoneRestService.updateMilestones(milestones).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateMilestone(MilestoneResource milestone) {
        return milestoneRestService.updateMilestone(milestone).toServiceResult();
    }

    @Override
    public MilestoneResource create(MilestoneType type, Long competitionId) {
        return milestoneRestService.create(type, competitionId).getSuccessObjectOrThrowException();
    }
}
