package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.form.ApplicationExpressionOfInterestForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupFormPopulator;
import org.springframework.stereotype.Service;

@Service
public class ApplicationExpressionOfInterestFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST;
    }

    @Override
    public ApplicationExpressionOfInterestForm populateForm(CompetitionResource competitionResource) {
        return new ApplicationExpressionOfInterestForm(competitionResource.isEnabledForPreRegistration());
    }
}
