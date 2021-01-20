package org.innovateuk.ifs.management.competition.setup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.completionstage.viewmodel.CompletionStageViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompletionStageViewModelPopulatorTest {

    @Mock
    private CompletionStageUtils completionStageUtils;

    @InjectMocks
    private CompletionStageViewModelPopulator completionStageViewModelPopulator;

    @Before
    public void setup() {
        when(completionStageUtils.isAlwaysOpenCompetitionEnabled()).thenReturn(true);
    }

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, competition,
                CompetitionSetupSection.COMPLETION_STAGE, null, false, false);

        when(completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.RELEASE_FEEDBACK)).thenReturn(true);

        CompletionStageViewModel viewModel = completionStageViewModelPopulator.populateModel(generalSetupViewModel, competition);

        assertEquals(CompetitionSetupSection.COMPLETION_STAGE, viewModel.getGeneral().getCurrentSection());
        assertThat(viewModel.getCompetitionCloseCompletionStage()).isEqualTo(CompetitionCompletionStage.COMPETITION_CLOSE);
        assertThat(viewModel.getReleaseFeedbackCompletionStage()).isEqualTo(CompetitionCompletionStage.RELEASE_FEEDBACK);
        assertThat(viewModel.getProjectSetupCompletionStage()).isEqualTo(CompetitionCompletionStage.PROJECT_SETUP);
        assertThat(viewModel.isAlwaysOpenCompetitionEnabled()).isEqualTo(true);
        assertThat(viewModel.isApplicationSubmissionEnabled()).isEqualTo(true);
    }

    @Test
    public void populateModelForCompetitionClose() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE).
                build();

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, competition,
                CompetitionSetupSection.COMPLETION_STAGE, null, false, false);

        when(completionStageUtils.isApplicationSubmissionEnabled(CompetitionCompletionStage.COMPETITION_CLOSE)).thenReturn(false);

        CompletionStageViewModel viewModel = completionStageViewModelPopulator.populateModel(generalSetupViewModel, competition);

        assertEquals(CompetitionSetupSection.COMPLETION_STAGE, viewModel.getGeneral().getCurrentSection());
        assertThat(viewModel.getCompetitionCloseCompletionStage()).isEqualTo(CompetitionCompletionStage.COMPETITION_CLOSE);
        assertThat(viewModel.getReleaseFeedbackCompletionStage()).isEqualTo(CompetitionCompletionStage.RELEASE_FEEDBACK);
        assertThat(viewModel.getProjectSetupCompletionStage()).isEqualTo(CompetitionCompletionStage.PROJECT_SETUP);
        assertThat(viewModel.isAlwaysOpenCompetitionEnabled()).isEqualTo(true);
        assertThat(viewModel.isApplicationSubmissionEnabled()).isEqualTo(false);
    }

    @Test
    public void sectionToPopulateModel() {
        assertThat(completionStageViewModelPopulator.sectionToPopulateModel()).isEqualTo(CompetitionSetupSection.COMPLETION_STAGE);
    }
}
