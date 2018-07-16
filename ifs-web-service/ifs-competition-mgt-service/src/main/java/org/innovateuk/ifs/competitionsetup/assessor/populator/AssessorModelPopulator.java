package org.innovateuk.ifs.competitionsetup.assessor.populator;

import org.innovateuk.ifs.competition.resource.AssessorCountOptionResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.AssessorCountOptionsRestService;
import org.innovateuk.ifs.competitionsetup.assessor.viewmodel.AssessorViewModel;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * populates the model for the assessor competition setup section.
 */
@Service
public class AssessorModelPopulator implements CompetitionSetupSectionModelPopulator {

	private AssessorCountOptionsRestService assessorCountOptionsRestService;

	public AssessorModelPopulator(AssessorCountOptionsRestService assessorCountOptionsRestService) {
		this.assessorCountOptionsRestService = assessorCountOptionsRestService;
	}

	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		List<AssessorCountOptionResource> assessorCountOptions =  assessorCountOptionsRestService.
				findAllByCompetitionType(competitionResource.getCompetitionType()).getSuccess();

		return new AssessorViewModel(generalViewModel,
				assessorCountOptions,
                competitionResource.isAssessmentClosed(),
                competitionResource.isSetupAndAfterNotifications());
	}
}
