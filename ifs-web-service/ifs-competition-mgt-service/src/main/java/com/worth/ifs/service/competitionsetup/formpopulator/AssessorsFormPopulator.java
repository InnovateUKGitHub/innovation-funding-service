package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.AssessorsForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

/**
 * Form populator for the assessors form competition setup section.
 */
@Service
public class AssessorsFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		AssessorsForm competitionSetupForm = new AssessorsForm();

		return competitionSetupForm;
	}

}
