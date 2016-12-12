package org.innovateuk.ifs.application.service;

import java.util.ArrayList;
import java.util.List;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;

import org.innovateuk.ifs.commons.error.Error;

/**
 * This class contains methods to retrieve and store {@link MilestoneResource} related data,
 * through the RestService {@link MilestoneRestService}.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService{

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public List<MilestoneResource> getAllMilestonesByCompetitionId(Long competitionId) {
        return milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public MilestoneResource getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId) {
        return milestoneRestService.getMilestoneByTypeAndCompetitionId(type, competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<Error> updateMilestones(List<MilestoneResource> milestones) {
       RestResult<Void> result = milestoneRestService.updateMilestones(milestones);
       if(result.isFailure()) {
    	   return result.getFailure().getErrors();
       }
       return new ArrayList<>();
    }

    @Override
    public List<Error> updateMilestone(MilestoneResource milestone) {
        RestResult<Void> result = milestoneRestService.updateMilestone(milestone);
        if(result.isFailure()) {
            return result.getFailure().getErrors();
        }
        return new ArrayList<>();
    }

    @Override
    public MilestoneResource create(MilestoneType type, Long competitionId) {
        return milestoneRestService.create(type, competitionId).getSuccessObjectOrThrowException();
    }
}
