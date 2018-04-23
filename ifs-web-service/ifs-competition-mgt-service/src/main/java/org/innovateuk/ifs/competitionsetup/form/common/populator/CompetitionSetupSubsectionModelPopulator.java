package org.innovateuk.ifs.competitionsetup.form.common.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.CompetitionSetupSubsectionViewModel;

import java.util.Optional;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSubsectionModelPopulator {

	CompetitionSetupSubsection sectionToPopulateModel();
	
	CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId);
}
