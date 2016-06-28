package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.ApplicationFormForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ApplicationFormFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		ApplicationFormForm competitionSetupForm = new ApplicationFormForm();

		return competitionSetupForm;
	}

}
