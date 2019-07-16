package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;

import java.util.List;
import java.util.Map;

/**
 * service for logic around handling the milestones of competitions in the setup phase.
 */
public interface CompetitionSetupMilestoneService {

	ServiceResult<List<MilestoneResource>> createMilestonesForIFSCompetition(Long competitionId);

    ServiceResult<Void> updateMilestonesForCompetition(List<MilestoneResource> milestones, Map<String, GenericMilestoneRowForm> milestoneEntries, Long competitionId);

	List<Error> validateMilestoneDates(Map<String, GenericMilestoneRowForm> milestonesFormEntries);

	void sortMilestones(MilestonesForm MilestoneForm);

	boolean isMilestoneDateValid(Integer day, Integer month, Integer year);
}
