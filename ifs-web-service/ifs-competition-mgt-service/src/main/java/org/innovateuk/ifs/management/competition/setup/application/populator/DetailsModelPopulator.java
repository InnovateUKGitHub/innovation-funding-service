package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.viewmodel.DetailsViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * populates the model for the Application Details sub-section under the Application of competition setup section.
 */
@Service
public class DetailsModelPopulator implements CompetitionSetupSubsectionModelPopulator {

	@Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.APPLICATION_DETAILS;
	}

	@Override
	public CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId) {
        return new DetailsViewModel();
    }
}
