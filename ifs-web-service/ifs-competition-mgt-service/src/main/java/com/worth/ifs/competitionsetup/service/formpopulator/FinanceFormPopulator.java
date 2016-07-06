package com.worth.ifs.competitionsetup.service.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.FinanceForm;

/**
 * Form populator for the finance competition setup section.
 */
@Service
public class FinanceFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.FINANCE;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		FinanceForm competitionSetupForm = new FinanceForm();

		return competitionSetupForm;
	}

}
