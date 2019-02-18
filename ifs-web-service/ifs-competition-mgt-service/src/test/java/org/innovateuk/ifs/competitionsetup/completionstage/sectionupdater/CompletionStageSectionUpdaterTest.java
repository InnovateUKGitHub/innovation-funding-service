package org.innovateuk.ifs.competitionsetup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompletionStageSectionUpdaterTest {

    @InjectMocks
    private CompletionStageSectionUpdater updater;

    @Mock
    private MilestoneRestService milestoneRestServiceMock;

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
    public void supportsFrom() {
        assertThat(updater.supportsForm(CompletionStageForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
