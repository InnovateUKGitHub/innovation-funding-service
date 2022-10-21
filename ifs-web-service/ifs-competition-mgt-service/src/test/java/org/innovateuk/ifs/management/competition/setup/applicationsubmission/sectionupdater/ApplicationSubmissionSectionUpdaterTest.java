package org.innovateuk.ifs.management.competition.setup.applicationsubmission.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;

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

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form, loggedInUser);

        assertThat(updateResult.isSuccess()).isTrue();

        verify(competitionSetupRestService, times(1)).update(competition);
    }

    @Test
    public void getNextSection() {

        ReflectionTestUtils.setField(updater, "isExpressionOfInterestEnabled", true);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(false)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.MILESTONES);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application-expression-of-interest");
    }

    @Test
    public void getNextSectionExpressionOfInterestNotEnabled() {

        ReflectionTestUtils.setField(updater, "isExpressionOfInterestEnabled", false);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(false)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.MILESTONES);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/milestones");
    }

    @Test
    public void getNextSectionAlwaysOpen() {

        ReflectionTestUtils.setField(updater, "isAssessmentStageEnabled", true);
        ReflectionTestUtils.setField(updater, "isExpressionOfInterestEnabled", true);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(true)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application-expression-of-interest");
    }

    @Test
    public void getNextSectionAlwaysOpenExpressionOfInterestNotEnabled() {

        ReflectionTestUtils.setField(updater, "isAssessmentStageEnabled", true);
        ReflectionTestUtils.setField(updater, "isExpressionOfInterestEnabled", false);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(true)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.APPLICATION_ASSESSMENT);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application-assessment");
    }

    @Test
    public void getNextSectionAlwaysOpenAssessmentStageNotEnabled() {

        ReflectionTestUtils.setField(updater, "isAssessmentStageEnabled", false);
        ReflectionTestUtils.setField(updater, "isExpressionOfInterestEnabled", false);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(true)
                .build();
        ApplicationSubmissionForm form = new ApplicationSubmissionForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.MILESTONES);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/milestones");
    }

    @Test
    public void supportsFrom() {
        assertThat(updater.supportsForm(ApplicationSubmissionForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
