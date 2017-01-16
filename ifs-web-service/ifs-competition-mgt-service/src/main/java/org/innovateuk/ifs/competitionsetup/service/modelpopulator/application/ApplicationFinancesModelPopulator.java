package org.innovateuk.ifs.competitionsetup.service.modelpopulator.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * populates the model for the Finances sub-section under the Application of competition setup section.
 */
@Service
public class ApplicationFinancesModelPopulator implements CompetitionSetupSubsectionModelPopulator {

	@Autowired
	public CompetitionSetupFinanceService competitionSetupFinanceService;

	@Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.FINANCES;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId) {
		CompetitionSetupFinanceResource compSetupFinanceRes = competitionSetupFinanceService.getByCompetitionId(competitionResource.getId()).getSuccessObjectOrThrowException();
		ApplicationFinanceForm form = new ApplicationFinanceForm();
		form.setFullApplicationFinance(compSetupFinanceRes.isFullApplicationFinance());
		form.setIncludeGrowthTable(compSetupFinanceRes.isIncludeGrowthTable());
		model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
		model.addAttribute(COMPETITION_ID_KEY, competitionResource.getId());
	}
}
