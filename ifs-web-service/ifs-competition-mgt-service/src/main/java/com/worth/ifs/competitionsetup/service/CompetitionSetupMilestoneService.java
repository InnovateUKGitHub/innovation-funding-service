package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
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
