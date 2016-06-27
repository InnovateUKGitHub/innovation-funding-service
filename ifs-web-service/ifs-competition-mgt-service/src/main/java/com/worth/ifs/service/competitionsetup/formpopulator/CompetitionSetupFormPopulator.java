package com.worth.ifs.service.competitionsetup.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

	CompetitionSetupSection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
