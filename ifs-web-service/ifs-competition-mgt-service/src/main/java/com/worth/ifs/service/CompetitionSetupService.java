package com.worth.ifs.service;

import org.springframework.ui.Model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

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
}
