package org.innovateuk.ifs.competitionsetup.form.common.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.common.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

	CompetitionSetupSection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
