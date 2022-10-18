package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.populator.ApplicationSubmissionFormPopulator;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class ApplicationExpressionOfInterestFormPopulatorTest {

    private ApplicationExpressionOfInterestFormPopulator applicationExpressionOfInterestFormPopulator;

    @Before
    public void setup() {
        applicationExpressionOfInterestFormPopulator = new ApplicationExpressionOfInterestFormPopulator();
    }

    @Test
    public void populateForm() {

        CompetitionResource competition = newCompetitionResource().
                withAlwaysOpen(true).
                build();

        ApplicationSubmissionForm form = new ApplicationSubmissionFormPopulator().populateForm(competition);

        assertThat(form.getAlwaysOpen()).isEqualTo(true);
        assertThat(form.isMarkAsCompleteAction()).isTrue();
    }

    @Test
    public void sectionToFill() {
        assertThat(applicationExpressionOfInterestFormPopulator.sectionToFill()).isEqualTo(CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST);
    }
}
