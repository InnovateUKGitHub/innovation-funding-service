package org.innovateuk.ifs.management.competition.setup.applicationsubmission.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Application Submission form in Competition Setup.
 */
@Service
public class ApplicationSubmissionFormPopulator implements CompetitionSetupFormPopulator  {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.APPLICATION_SUBMISSION;
    }

    @Override
    public ApplicationSubmissionForm populateForm(CompetitionResource competitionResource) {
        return new ApplicationSubmissionForm(competitionResource.getAlwaysOpen());
    }
}
