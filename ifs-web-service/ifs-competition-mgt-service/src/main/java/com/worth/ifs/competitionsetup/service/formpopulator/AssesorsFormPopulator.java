package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.competitionsetup.utils.CompetitionUtils;
import org.springframework.stereotype.Service;

/**
 * Form populator for the assessors competition setup section.
 */
@Service
public class AssesorsFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		AssessorsForm assessorsForm = new AssessorsForm();
		return assessorsForm;
	}


}
