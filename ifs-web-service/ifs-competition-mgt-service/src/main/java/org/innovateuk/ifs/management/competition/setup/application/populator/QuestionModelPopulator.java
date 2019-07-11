package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class QuestionModelPopulator implements CompetitionSetupSubsectionModelPopulator {

    @Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId) {
        return new QuestionViewModel();
    }
}
