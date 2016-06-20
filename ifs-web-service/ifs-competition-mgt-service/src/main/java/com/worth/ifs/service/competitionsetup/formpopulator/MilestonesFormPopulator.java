package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.MilestonesForm;

@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		MilestonesForm competitionSetupForm = new MilestonesForm();

		return competitionSetupForm;
	}

}
