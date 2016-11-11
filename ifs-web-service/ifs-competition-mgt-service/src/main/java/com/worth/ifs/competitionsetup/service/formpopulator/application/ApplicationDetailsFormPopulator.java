package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import com.worth.ifs.competitionsetup.utils.CompetitionUtils;
import com.worth.ifs.competitionsetup.viewmodel.application.QuestionViewModel;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.service.FormInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.inputsTypeMatching;

/**
 * Form populator for the Application Details sub-section under the Application form of competition setup section.
 */
@Service
public class ApplicationDetailsFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.APPLICATION_DETAILS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {
		ApplicationDetailsForm competitionSetupForm = new ApplicationDetailsForm();

		competitionSetupForm.setUseProjectTitleQuestion(competitionResource.isUseProjectTitleQuestion());
		competitionSetupForm.setUseResubmissionQuestion(competitionResource.isUseResubmissionQuestion());
		competitionSetupForm.setUseEstimatedStartDateQuestion(competitionResource.isUseEstimatedStartDateQuestion());
		competitionSetupForm.setUseDurationQuestion(competitionResource.isUseDurationQuestion());

		return competitionSetupForm;
	}


}
