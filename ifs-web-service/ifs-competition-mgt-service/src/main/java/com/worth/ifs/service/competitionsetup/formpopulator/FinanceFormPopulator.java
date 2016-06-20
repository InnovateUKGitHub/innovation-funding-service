package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.FinanceForm;

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
