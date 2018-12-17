package org.innovateuk.ifs.competitionsetup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competitionsetup.completionstage.viewmodel.CompletionStageViewModel;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class CompletionStageViewModelPopulatorTest {

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        CompletionStageViewModel viewModel =
                new CompletionStageViewModelPopulator().populateModel(null, competition);

        assertThat(viewModel.getNonSelectableMilestones()).containsExactly(
                MilestoneType.OPEN_DATE,
                MilestoneType.BRIEFING_EVENT,
                MilestoneType.SUBMISSION_DATE,
                MilestoneType.ALLOCATE_ASSESSORS,
                MilestoneType.ASSESSOR_BRIEFING,
                MilestoneType.ASSESSOR_ACCEPTS,
                MilestoneType.ASSESSOR_DEADLINE,
                MilestoneType.LINE_DRAW,
                MilestoneType.ASSESSMENT_PANEL,
                MilestoneType.PANEL_DATE,
                MilestoneType.FUNDERS_PANEL,
                MilestoneType.NOTIFICATIONS);

        assertThat(viewModel.getReleaseFeedbackCompletionStage()).isEqualTo(CompetitionCompletionStage.RELEASE_FEEDBACK);
        assertThat(viewModel.getProjectSetupCompletionStage()).isEqualTo(CompetitionCompletionStage.PROJECT_SETUP);
    }

    @Test
    public void sectionToPopulateModel() {
        assertThat(new CompletionStageViewModelPopulator().sectionToPopulateModel()).
                isEqualTo(CompetitionSetupSection.COMPLETION_STAGE);
    }
}
