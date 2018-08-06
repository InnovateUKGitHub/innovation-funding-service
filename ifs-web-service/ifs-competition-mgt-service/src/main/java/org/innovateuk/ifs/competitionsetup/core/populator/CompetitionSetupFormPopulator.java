package org.innovateuk.ifs.competitionsetup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

    CompetitionSetupSection sectionToFill();

    CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
