package org.innovateuk.ifs.management.competition.setup.assessor.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
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

		competitionSetupForm.setAssessorCount(competitionResource.getCompetitionAssessmentConfig().getAssessorCount());
		competitionSetupForm.setAssessorPay(competitionResource.getCompetitionAssessmentConfig().getAssessorPay() != null ? competitionResource.getCompetitionAssessmentConfig().getAssessorPay() : BigDecimal.ZERO);
		competitionSetupForm.setHasAssessmentPanel(competitionResource.getCompetitionAssessmentConfig().getHasAssessmentPanel());
		competitionSetupForm.setHasInterviewStage(competitionResource.getCompetitionAssessmentConfig().getHasInterviewStage());
		competitionSetupForm.setAssessorFinanceView(competitionResource.getCompetitionAssessmentConfig().getAssessorFinanceView());
		competitionSetupForm.setAverageAssessorScore(competitionResource.getCompetitionAssessmentConfig().getAverageAssessorScore());

		return competitionSetupForm;
	}
}
