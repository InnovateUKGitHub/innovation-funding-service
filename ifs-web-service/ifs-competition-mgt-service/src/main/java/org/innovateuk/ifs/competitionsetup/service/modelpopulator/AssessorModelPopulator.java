package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * populates the model for the assessor competition setup section.
 */
@Service
public class AssessorModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		model.addAttribute("assessorOptions", competitionService.getAssessorOptionsForCompetitionType(competitionResource.getCompetitionType()));
		model.addAttribute("isAssessmentClosed", competitionResource.isAssessmentClosed());
	}
}
