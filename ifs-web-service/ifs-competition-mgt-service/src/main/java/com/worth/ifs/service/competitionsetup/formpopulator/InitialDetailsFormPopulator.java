package com.worth.ifs.service.competitionsetup.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.InitialDetailsForm;

@Service
public class InitialDetailsFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		InitialDetailsForm competitionSetupForm = new InitialDetailsForm();

		competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
		competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

		competitionSetupForm.setInnovationAreaCategoryId(competitionResource.getInnovationArea());
		competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());

		if (competitionResource.getStartDate() != null) {
			competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
			competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
			competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
		}

		competitionSetupForm.setCompetitionCode(competitionResource.getCode());
		competitionSetupForm.setPafNumber(competitionResource.getPafCode());
		competitionSetupForm.setTitle(competitionResource.getName());
		competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

		return competitionSetupForm;
	}

}
