package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.common.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.common.populator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.common.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.application.viewmodel.ApplicationFinanceViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * populates the model for the Finances sub-section under the Application of competition setup section.
 */
@Service
public class ApplicationFinanceModelPopulator implements CompetitionSetupSubsectionModelPopulator {

	@Autowired
	public CompetitionSetupFinanceService competitionSetupFinanceService;

	@Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.FINANCES;
	}

	@Override
	public CompetitionSetupSubsectionViewModel populateModel(CompetitionResource competitionResource, Optional<Long> objectId) {
        return new ApplicationFinanceViewModel(
                "sector".equalsIgnoreCase(competitionResource.getCompetitionTypeName()),
                competitionResource.isNonFinanceType()
        );
    }
}
