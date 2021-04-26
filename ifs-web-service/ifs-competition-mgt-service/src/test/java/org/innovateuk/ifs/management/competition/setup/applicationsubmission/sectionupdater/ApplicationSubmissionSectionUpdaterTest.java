package org.innovateuk.ifs.management.competition.setup.applicationsubmission.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationSubmissionSectionUpdaterTest {

    @InjectMocks
    private ApplicationSubmissionSectionUpdater updater;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Test
    public void doSaveSection() {

        CompetitionResource competition = newCompetitionResource()
                .withAlwaysOpen(true)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form);

        assertThat(updateResult.isSuccess()).isTrue();

        verify(competitionSetupRestService, times(1)).update(competition);
    }

    @Test
    public void getNextSection() {

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.APPLICATION_SUBMISSION);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/milestones");
    }

    @Test
    public void supportsFrom() {
        assertThat(updater.supportsForm(ApplicationSubmissionForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
