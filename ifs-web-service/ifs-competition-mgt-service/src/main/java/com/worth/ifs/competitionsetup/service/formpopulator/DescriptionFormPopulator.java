package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.DescriptionForm;
import org.springframework.stereotype.Service;

/**
 * Form model for the description and brief competition setup section.
 */
@Service
public class DescriptionFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.DESCRIPTION_AND_BRIEF;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		DescriptionForm competitionSetupForm = new DescriptionForm();

		return competitionSetupForm;
	}

}
