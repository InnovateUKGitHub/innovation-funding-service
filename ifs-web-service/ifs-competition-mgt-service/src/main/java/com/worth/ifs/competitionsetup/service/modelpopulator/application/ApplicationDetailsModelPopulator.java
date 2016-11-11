package com.worth.ifs.competitionsetup.service.modelpopulator.application;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSubsectionModelPopulator;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

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
	public void populateModel(Model model, CompetitionResource competitionResource, Optional<Long> objectId) {
		ApplicationDetailsForm form = new ApplicationDetailsForm();
		form.setUseProjectTitleQuestion(competitionResource.isUseProjectTitleQuestion());
		form.setUseResubmissionQuestion(competitionResource.isUseResubmissionQuestion());
		form.setUseEstimatedStartDateQuestion(competitionResource.isUseEstimatedStartDateQuestion());
		form.setUseDurationQuestion(competitionResource.isUseDurationQuestion());
		model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
		model.addAttribute(COMPETITION_ID_KEY, competitionResource.getId());
	}
}
