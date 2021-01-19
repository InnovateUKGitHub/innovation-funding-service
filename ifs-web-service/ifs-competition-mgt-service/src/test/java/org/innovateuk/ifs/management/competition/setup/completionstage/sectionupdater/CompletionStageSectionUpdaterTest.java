package org.innovateuk.ifs.management.competition.setup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.COMPETITION_CLOSE;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompletionStageSectionUpdaterTest {

    @InjectMocks
    private CompletionStageSectionUpdater updater;

    @Mock
    private MilestoneRestService milestoneRestServiceMock;

    @Mock
    private CompletionStageUtils completionStageUtils;

    @Test
    public void doSaveSection() {

        CompetitionResource competition = newCompetitionResource().build();
        CompletionStageForm form = new CompletionStageForm(PROJECT_SETUP);

        when(milestoneRestServiceMock.updateCompletionStage(competition.getId(), PROJECT_SETUP)).
                thenReturn(restSuccess());

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form);

        assertThat(updateResult.isSuccess()).isTrue();

        verify(milestoneRestServiceMock, times(1)).updateCompletionStage(competition.getId(), PROJECT_SETUP);
    }

    @Test
    public void getNextSectionWhenApplicationSubmissionEnabled() {

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();
        CompletionStageForm form = new CompletionStageForm(PROJECT_SETUP);

        when(completionStageUtils.isApplicationSubmissionEnabled(PROJECT_SETUP)).thenReturn(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.COMPLETION_STAGE);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application-submission");
    }

    @Test
    public void getNextSectionWhenApplicationSubmissionNotEnabled() {

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();
        CompletionStageForm form = new CompletionStageForm(COMPETITION_CLOSE);

        when(completionStageUtils.isApplicationSubmissionEnabled(COMPETITION_CLOSE)).thenReturn(false);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.COMPLETION_STAGE);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/milestones");
    }

    @Test
    public void supportsFrom() {
        assertThat(updater.supportsForm(CompletionStageForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
