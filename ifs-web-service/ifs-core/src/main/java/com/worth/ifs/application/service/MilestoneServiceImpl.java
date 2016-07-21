package com.worth.ifs.application.service;

import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.service.MilestoneRestService;
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
    public List<MilestoneResource> getAllDatesByCompetitionId(Long competitionId) {
        return milestoneRestService.getAllDatesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<ValidationMessages> update(List<MilestoneResource> milestones, Long competitionId) {
       return milestoneRestService.update(milestones, competitionId).getSuccessObject();
    }

    @Override
    public MilestoneResource create() {
        return milestoneRestService.create().getSuccessObjectOrThrowException();
    }
}
