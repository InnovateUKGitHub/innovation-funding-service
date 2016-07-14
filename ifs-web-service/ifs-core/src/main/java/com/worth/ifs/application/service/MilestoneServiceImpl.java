package com.worth.ifs.application.service;

import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.service.MilestoneRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Joe on 14/07/2016.
 */
@Service
public class MilestoneServiceImpl implements MilestoneService{

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Override
    public List<MilestoneResource> getAllDatesByCompetitionId(Long competitionId) {
        return milestoneRestService.getAllDatesByCompetitionId(competitionId).getSuccessObjectOrThrowException();
    }
}
