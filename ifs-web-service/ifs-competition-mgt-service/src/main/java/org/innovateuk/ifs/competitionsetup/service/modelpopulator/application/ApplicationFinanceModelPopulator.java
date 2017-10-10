package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.application.ApplicationFinanceViewModel;
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
        return new ApplicationFinanceViewModel("sector".equalsIgnoreCase(competitionResource.getCompetitionTypeName()));
    }
}
