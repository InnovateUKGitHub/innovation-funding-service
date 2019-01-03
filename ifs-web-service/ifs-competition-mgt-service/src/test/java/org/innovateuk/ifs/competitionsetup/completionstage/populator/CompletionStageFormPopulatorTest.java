package org.innovateuk.ifs.competitionsetup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.completionstage.form.CompletionStageForm;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class CompletionStageFormPopulatorTest {

    @Test
    public void populateForm() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        CompletionStageForm form = new CompletionStageFormPopulator().populateForm(competition);

        assertThat(form.getSelectedCompletionStage()).isEqualTo(CompetitionCompletionStage.RELEASE_FEEDBACK);
        assertThat(form.isAutoSaveAction()).isFalse();
        assertThat(form.isMarkAsCompleteAction()).isTrue();
    }

    @Test
    public void populateFormWithCompletionStageNotYetSelected() {
        CompetitionResource competition = newCompetitionResource().build();
        CompletionStageForm form = new CompletionStageFormPopulator().populateForm(competition);
        assertThat(form.getSelectedCompletionStage()).isNull();;
    }

    @Test
    public void sectionToFill() {
        assertThat(new CompletionStageFormPopulator().sectionToFill()).isEqualTo(CompetitionSetupSection.COMPLETION_STAGE);
    }
}
