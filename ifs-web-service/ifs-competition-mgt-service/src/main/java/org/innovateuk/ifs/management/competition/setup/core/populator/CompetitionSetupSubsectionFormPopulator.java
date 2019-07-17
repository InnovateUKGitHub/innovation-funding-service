package org.innovateuk.ifs.management.competition.setup.core.populator;


import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import java.util.Optional;

public interface CompetitionSetupSubsectionFormPopulator {

	CompetitionSetupSubsection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId);
}
