package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
public interface MilestoneRestService {

    RestResult<List<MilestoneResource>> getAllPublicMilestonesByCompetitionId(Long competitionId);

    RestResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long competitionId);

    RestResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long CompetitionId);

    RestResult<Void> updateMilestones(List<MilestoneResource> milestones);

    RestResult<Void> updateMilestone(MilestoneResource milestone);

    RestResult<MilestoneResource> create(MilestoneType type, Long competitionId);

    RestResult<Void> resetMilestone(MilestoneResource milestone);
}
