package com.worth.ifs.competition.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
public interface MilestoneRestService {

    RestResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long competitionId);

    RestResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long CompetitionId);

    RestResult<Void> updateMilestones(List<MilestoneResource> milestones, Long competitionId);

    RestResult<Void> updateMilestone(MilestoneResource milestone);

    RestResult<MilestoneResource> create(MilestoneType type, Long competitionId);
}
