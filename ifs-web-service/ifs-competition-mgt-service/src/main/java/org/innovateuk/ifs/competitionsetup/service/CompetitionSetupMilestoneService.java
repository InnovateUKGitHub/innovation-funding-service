package org.innovateuk.ifs.competitionsetup.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;

/**
 * service for logic around handling the milestones of competitions in the setup phase.
 */
public interface CompetitionSetupMilestoneService {

	List<MilestoneResource> createMilestonesForCompetition(Long competitionId);

    List<Error> updateMilestonesForCompetition(List<MilestoneResource> milestones, LinkedMap<String, MilestoneViewModel> milestoneEntries, Long competitionId);

	List<Error> validateMilestoneDates(LinkedMap<String, MilestoneViewModel> milestonesFormEntries);

	void sortMilestones(MilestonesForm MilestoneForm);

	Boolean isMilestoneDateValid(Integer day, Integer month, Integer year);
}
