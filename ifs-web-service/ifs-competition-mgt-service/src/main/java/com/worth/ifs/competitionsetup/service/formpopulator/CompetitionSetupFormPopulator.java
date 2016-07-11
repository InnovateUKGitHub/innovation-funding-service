package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

	CompetitionSetupSection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
