package org.innovateuk.ifs.management.competition.setup.applicationsubmission.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class ApplicationSubmissionFormPopulatorTest {

    private ApplicationSubmissionFormPopulator applicationSubmissionFormPopulator;

    @Before
    public void setup() {
        applicationSubmissionFormPopulator = new ApplicationSubmissionFormPopulator();
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
        assertThat(applicationSubmissionFormPopulator.sectionToFill()).isEqualTo(CompetitionSetupSection.APPLICATION_SUBMISSION);
    }
}
