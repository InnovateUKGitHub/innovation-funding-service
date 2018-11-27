package org.innovateuk.ifs.competitionsetup.completionstage.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupMilestoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompletionStageSectionUpdaterTest {

    @InjectMocks
    private CompletionStageSectionUpdater updater;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneServiceMock;

    @Test
    public void doSaveSection() {

        CompetitionResource competition = newCompetitionResource().build();
        CompletionStageForm form = new CompletionStageForm(PROJECT_SETUP);

        when(competitionSetupMilestoneServiceMock.updateCompletionStage(competition.getId(), PROJECT_SETUP)).
                thenReturn(serviceSuccess());

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form);

        assertThat(updateResult.isSuccess()).isTrue();

        verify(competitionSetupMilestoneServiceMock, times(1)).updateCompletionStage(competition.getId(), PROJECT_SETUP);
    }

    @Test
    public void supportsFrom() {
        assertThat(updater.supportsForm(CompletionStageForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
