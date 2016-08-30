package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.model.Funder;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.stereotype.Service;

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

		competitionSetupForm.setActivityCode(competitionResource.getActivityCode());
		competitionSetupForm.setInnovateBudget(competitionResource.getInnovateBudget());

		competitionSetupForm.setCompetitionCode(competitionResource.getCode());
		competitionSetupForm.setPafNumber(competitionResource.getPafCode());
		competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

		competitionResource.getFunders().forEach(funderResource ->  {
			Funder funder = new Funder();
			funder.setFunder(funderResource.getFunder());
			funder.setFunderBudget(funderResource.getFunderBudget());
			funder.setCoFunder(funderResource.getCoFunder());
			competitionSetupForm.getFunders().add(funder);
		});

		return competitionSetupForm;
	}

}
