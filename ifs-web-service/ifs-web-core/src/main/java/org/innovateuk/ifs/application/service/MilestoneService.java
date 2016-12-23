package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for CRUD operations on {@link MilestoneResource} related data.
 */
@Service
public interface MilestoneService {

    List<MilestoneResource> getAllMilestonesByCompetitionId(Long competitionId);

    MilestoneResource getMilestoneByTypeAndCompetitionId(MilestoneType type, Long competitionId);

    List<Error> updateMilestones(List<MilestoneResource> milestones);

    List<Error> updateMilestone(MilestoneResource milestone);

    MilestoneResource create(MilestoneType type, Long competitionId);
}
