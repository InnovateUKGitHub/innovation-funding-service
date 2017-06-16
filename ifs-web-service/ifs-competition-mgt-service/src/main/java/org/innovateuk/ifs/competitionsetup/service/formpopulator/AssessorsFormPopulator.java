package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
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
		competitionSetupForm.setUseAssessmentPanel(convertToPublishSettingString(competitionResource.isUseAssessmentPanel()));

		return competitionSetupForm;
	}

	private String convertToPublishSettingString(Boolean useAssessmentPanel) {
		if(null == useAssessmentPanel) {
			return "";
		}
		return useAssessmentPanel ? "usePanel" : "noPanel";
	}
}
