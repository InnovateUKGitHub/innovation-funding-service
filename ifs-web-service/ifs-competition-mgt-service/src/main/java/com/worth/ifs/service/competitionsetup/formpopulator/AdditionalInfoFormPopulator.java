package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.AdditionalInfoForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

/**
 * Form populator for the additional info competition setup section.
 */
@Service
public class AdditionalInfoFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		AdditionalInfoForm competitionSetupForm = new AdditionalInfoForm();

		return competitionSetupForm;
	}

}
