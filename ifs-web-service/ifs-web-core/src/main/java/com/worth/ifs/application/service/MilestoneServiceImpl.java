package com.worth.ifs.application.service;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.competition.resource.MilestoneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.service.MilestoneRestService;

import com.worth.ifs.commons.error.Error;

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
    public List<Error> updateMilestones(List<MilestoneResource> milestones, Long competitionId) {
       RestResult<Void> result = milestoneRestService.updateMilestones(milestones, competitionId);
       if(result.isFailure()) {
    	   return result.getFailure().getErrors();
       }
       return new ArrayList<>();
    }

    @Override
    public List<Error> updateMilestone(MilestoneResource milestone, Long competitionId) {
        RestResult<Void> result = milestoneRestService.updateMilestone(milestone, competitionId);
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
