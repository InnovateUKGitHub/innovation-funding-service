package com.worth.ifs.competitionsetup.service.formpopulator;


import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

import java.util.Optional;

public interface CompetitionSetupSubsectionFormPopulator {

	CompetitionSetupSubsection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId);
}
