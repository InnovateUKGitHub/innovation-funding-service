package org.innovateuk.ifs.competitionsetup.common.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.common.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

	CompetitionSetupSection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
