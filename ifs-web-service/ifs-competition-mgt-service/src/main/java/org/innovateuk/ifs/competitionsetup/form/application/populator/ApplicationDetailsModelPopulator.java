package org.innovateuk.ifs.competitionsetup.form.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.common.populator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.form.application.viewmodel.ApplicationDetailsViewModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
	public CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId) {
        return new ApplicationDetailsViewModel();
    }
}
