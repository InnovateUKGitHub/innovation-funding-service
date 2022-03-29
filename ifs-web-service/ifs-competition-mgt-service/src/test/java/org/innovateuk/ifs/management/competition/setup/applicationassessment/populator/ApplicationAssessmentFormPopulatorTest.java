package org.innovateuk.ifs.management.competition.setup.applicationassessment.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationassessment.form.ApplicationAssessmentForm;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class ApplicationAssessmentFormPopulatorTest {

    private ApplicationAssessmentFormPopulator applicationAssessmentFormPopulator;

    @Before
    public void setup() {
        applicationAssessmentFormPopulator = new ApplicationAssessmentFormPopulator();
    }

    @Test
    public void populateForm() {

        CompetitionResource competition = newCompetitionResource()
                .withHasAssessmentStage(true)
                .build();

        ApplicationAssessmentForm form = new ApplicationAssessmentFormPopulator().populateForm(competition);

        assertThat(form.getAssessmentStage()).isEqualTo(true);
        assertThat(form.isMarkAsCompleteAction()).isTrue();
    }

    @Test
    public void sectionToFill() {
        assertThat(applicationAssessmentFormPopulator.sectionToFill()).isEqualTo(CompetitionSetupSection.APPLICATION_ASSESSMENT);
    }
}
