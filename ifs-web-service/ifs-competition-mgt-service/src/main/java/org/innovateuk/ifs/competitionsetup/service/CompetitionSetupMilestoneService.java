package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;

import java.util.List;
import java.util.Map;

/**
 * service for logic around handling the milestones of competitions in the setup phase.
 */
public interface CompetitionSetupMilestoneService {

	List<MilestoneResource> createMilestonesForCompetition(Long competitionId);

    List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, Map<String, MilestoneRowForm> milestoneEntries, Long competitionId);

	List<Error> validateMilestoneDates(Map<String, MilestoneRowForm> milestonesFormEntries);

	void sortMilestones(MilestonesForm MilestoneForm);

	Boolean isMilestoneDateValid(Integer day, Integer month, Integer year);
}
