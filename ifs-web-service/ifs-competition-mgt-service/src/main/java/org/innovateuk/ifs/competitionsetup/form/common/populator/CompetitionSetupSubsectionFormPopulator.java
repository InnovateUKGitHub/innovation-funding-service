package org.innovateuk.ifs.competitionsetup.form.common.populator;


import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.common.form.CompetitionSetupForm;

import java.util.Optional;

public interface CompetitionSetupSubsectionFormPopulator {

	CompetitionSetupSubsection sectionToFill();
	
	CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId);
}
