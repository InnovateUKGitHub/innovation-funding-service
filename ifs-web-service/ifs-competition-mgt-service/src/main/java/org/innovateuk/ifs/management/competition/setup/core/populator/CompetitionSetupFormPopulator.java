package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

public interface CompetitionSetupFormPopulator {

    CompetitionSetupSection sectionToFill();

    CompetitionSetupForm populateForm(CompetitionResource competitionResource);
}
