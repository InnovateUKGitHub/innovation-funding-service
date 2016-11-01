package com.worth.ifs.competitionsetup.service.modelpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import org.springframework.ui.Model;

import java.util.Optional;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSubsectionModelPopulator {

	CompetitionSetupSubsection sectionToPopulateModel();
	
	void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId);
}
