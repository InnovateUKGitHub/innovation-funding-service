package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.ui.Model;

/**
 * service for logic around handling the various sections of competition setup.
 */
public interface CompetitionSetupService {

	void populateCompetitionSectionModelAttributes(Model model, CompetitionResource competitionResource,
			CompetitionSetupSection section);
	
	CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
			CompetitionSetupSection section);

	void saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
			CompetitionResource competitionResource, CompetitionSetupSection section);

	boolean isCompetitionReadyToOpen(CompetitionResource competitionResource);

	void setCompetitionAsReadyToOpen(Long competitionId);

	void setCompetitionAsCompetitionSetup(Long competitionId);
}
