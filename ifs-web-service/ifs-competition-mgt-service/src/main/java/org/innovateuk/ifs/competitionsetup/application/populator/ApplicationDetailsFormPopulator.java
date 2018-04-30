package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

		competitionSetupForm.setMaxProjectDuration(convertIntegerToBigDecimal(competitionResource.getMaxProjectDuration()));
		competitionSetupForm.setMinProjectDuration(convertIntegerToBigDecimal(competitionResource.getMinProjectDuration()));
		competitionSetupForm.setUseResubmissionQuestion(competitionResource.getUseResubmissionQuestion());

		return competitionSetupForm;
	}

	private BigDecimal convertIntegerToBigDecimal(Integer integer) {
		return integer != null ? BigDecimal.valueOf(integer) : null; // Null safe conversion
	}
}
