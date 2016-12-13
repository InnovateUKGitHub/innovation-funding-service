package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_NAME_KEY;

/**
 * populates the model for the Application Details sub-section under the Application of competition setup section.
 */
@Service
public class ApplicationDetailsModelPopulator implements CompetitionSetupSubsectionModelPopulator {

	@Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.APPLICATION_DETAILS;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId) {

		model.addAttribute(COMPETITION_ID_KEY, competitionResource.getId());
		model.addAttribute(COMPETITION_NAME_KEY, competitionResource.getName());
	}
}
