package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * populates the model for the Finances sub-section under the Application of competition setup section.
 */
@Service
public class FinanceModelPopulator implements CompetitionSetupSubsectionModelPopulator {

    @Override
    public CompetitionSetupSubsection sectionToPopulateModel() {
        return CompetitionSetupSubsection.FINANCES;
    }

	@Override
	public CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId) {
        return new FinanceViewModel(
                competitionResource.isNonFinanceType(), competitionResource.isKtp()
        );
    }
}
