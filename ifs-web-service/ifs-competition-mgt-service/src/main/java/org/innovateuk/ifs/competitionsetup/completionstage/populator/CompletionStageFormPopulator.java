package org.innovateuk.ifs.competitionsetup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupFormPopulator;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Completion Stage form in Competition Setup.
 */
@Service
public class CompletionStageFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Override
    public CompletionStageForm populateForm(CompetitionResource competitionResource) {
        return new CompletionStageForm(competitionResource.getCompletionStage());
    }
}


