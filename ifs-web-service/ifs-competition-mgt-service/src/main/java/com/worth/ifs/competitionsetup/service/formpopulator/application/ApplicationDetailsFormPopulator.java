package com.worth.ifs.competitionsetup.service.formpopulator.application;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

		competitionSetupForm.setUseResubmissionQuestion(competitionResource.isUseResubmissionQuestion());

		return competitionSetupForm;
	}


}
