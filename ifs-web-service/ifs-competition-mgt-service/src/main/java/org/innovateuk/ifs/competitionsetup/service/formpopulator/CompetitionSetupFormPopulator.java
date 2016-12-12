package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

	CompetitionSetupSection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
