package org.innovateuk.ifs.management.competition.setup.applicationassessment.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationassessment.form.ApplicationAssessmentForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Application assessment form in Competition Setup.
 */
@Service
public class ApplicationAssessmentFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.APPLICATION_ASSESSMENT;
    }

    @Override
    public ApplicationAssessmentForm populateForm(CompetitionResource competitionResource) {
        return new ApplicationAssessmentForm(competitionResource.hasAssessmentStage());
    }
}
