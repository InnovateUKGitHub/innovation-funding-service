package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.springframework.ui.Model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSectionModelPopulator {

	CompetitionSetupSection sectionToPopulateModel();
	
	void populateModel(Model model, CompetitionResource competitionResource);
}
