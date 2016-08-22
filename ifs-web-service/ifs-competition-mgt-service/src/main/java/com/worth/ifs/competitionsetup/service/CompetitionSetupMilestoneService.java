package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.model.MilestoneEntry;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;

/**
 * service for logic around handling the milestones of competitions in the setup phase.
 */
public interface CompetitionSetupMilestoneService {

	List<MilestoneResource> createMilestonesForCompetition(Long competitionId);

    List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, LinkedMap<String, MilestoneEntry> milestoneEntries, Long competitionId);

	List<Error> validateMilestoneDates(LinkedMap<String, MilestoneEntry> milestonesFormEntries);
}
