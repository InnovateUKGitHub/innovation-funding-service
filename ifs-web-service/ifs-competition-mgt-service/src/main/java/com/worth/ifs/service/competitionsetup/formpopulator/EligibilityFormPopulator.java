package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.EligibilityForm;

/**
 * Form populator for the eligibity competition setup section.
 */
@Service
public class EligibilityFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		EligibilityForm competitionSetupForm = new EligibilityForm();

		return competitionSetupForm;
	}

}
