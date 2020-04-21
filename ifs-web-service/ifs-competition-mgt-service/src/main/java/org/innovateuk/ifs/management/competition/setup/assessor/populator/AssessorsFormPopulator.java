package org.innovateuk.ifs.management.competition.setup.assessor.populator;

import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Form populator for the assessors competition setup section.
 */
@Service
public class AssessorsFormPopulator implements CompetitionSetupFormPopulator {

	@Autowired
	private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competitionResource.getId()).getSuccess();

		AssessorsForm competitionSetupForm = new AssessorsForm();

		competitionSetupForm.setAssessorCount(competitionAssessmentConfigResource.getAssessorCount());
		competitionSetupForm.setAssessorPay(competitionAssessmentConfigResource.getAssessorPay() != null ? competitionAssessmentConfigResource.getAssessorPay() : BigDecimal.ZERO);
		competitionSetupForm.setHasAssessmentPanel(competitionAssessmentConfigResource.getHasAssessmentPanel());
		competitionSetupForm.setHasInterviewStage(competitionAssessmentConfigResource.getHasInterviewStage());
		competitionSetupForm.setAssessorFinanceView(competitionAssessmentConfigResource.getAssessorFinanceView());
		competitionSetupForm.setAverageAssessorScore(competitionAssessmentConfigResource.getAverageAssessorScore());

		return competitionSetupForm;
	}
}
