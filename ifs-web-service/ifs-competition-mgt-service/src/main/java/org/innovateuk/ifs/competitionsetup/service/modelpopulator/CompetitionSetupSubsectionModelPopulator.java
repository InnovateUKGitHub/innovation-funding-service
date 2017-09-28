package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

import java.util.Optional;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSubsectionModelPopulator {

	CompetitionSetupSubsection sectionToPopulateModel();
	
	CompetitionSetupSubsectionViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource, Optional<Long> objectId);
}
