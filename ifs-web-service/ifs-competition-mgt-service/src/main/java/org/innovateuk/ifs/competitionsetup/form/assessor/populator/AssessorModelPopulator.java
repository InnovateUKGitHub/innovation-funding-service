package org.innovateuk.ifs.competitionsetup.form.assessor.populator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.common.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.form.assessor.viewmodel.AssessorViewModel;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		return new AssessorViewModel(generalViewModel,
                competitionService.getAssessorOptionsForCompetitionType(competitionResource.getCompetitionType()),
                competitionResource.isAssessmentClosed(),
                competitionResource.isSetupAndAfterNotifications());
	}
}
