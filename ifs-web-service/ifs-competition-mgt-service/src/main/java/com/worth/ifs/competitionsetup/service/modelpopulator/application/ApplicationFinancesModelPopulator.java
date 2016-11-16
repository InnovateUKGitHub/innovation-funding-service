package com.worth.ifs.competitionsetup.service.modelpopulator.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * populates the model for the Finances sub-section under the Application of competition setup section.
 */
@Service
public class ApplicationFinancesModelPopulator implements CompetitionSetupSubsectionModelPopulator {

	@Override
	public CompetitionSetupSubsection sectionToPopulateModel() {
		return CompetitionSetupSubsection.FINANCES;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId) {
		ApplicationFinanceForm form = new ApplicationFinanceForm();
		form.setFullApplicationFinance(competitionResource.isFullApplicationFinance());
		form.setIncludeGrowthTable(competitionResource.isIncludeGrowthTable());
		model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
		model.addAttribute(COMPETITION_ID_KEY, competitionResource.getId());
	}
}
