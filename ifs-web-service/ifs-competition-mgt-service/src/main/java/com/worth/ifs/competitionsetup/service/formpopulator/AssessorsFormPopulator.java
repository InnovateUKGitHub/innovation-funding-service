package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Form populator for the assessors competition setup section.
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

		competitionSetupForm.setAssessorCount(competitionResource.getAssessorCount());
		competitionSetupForm.setAssessorPay(competitionResource.getAssessorPay() != null ? competitionResource.getAssessorPay() : BigDecimal.ZERO);

		return competitionSetupForm;
	}
}
