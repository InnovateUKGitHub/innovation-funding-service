package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.form.ApplicationExpressionOfInterestForm;
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
public class ApplicationExpressionOfInterestSectionUpdaterTest {

    @InjectMocks
    private ApplicationExpressionOfInterestSectionUpdater updater;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Test
    public void doSaveSection() {

        CompetitionResource competition = newCompetitionResource()
                .withEnabledForExpressionOfInterest(true)
                .build();

        ApplicationExpressionOfInterestForm form = new ApplicationExpressionOfInterestForm(true);

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        ServiceResult<Void> updateResult = updater.doSaveSection(competition, form, loggedInUser);

        assertThat(updateResult.isSuccess()).isTrue();

        verify(competitionSetupRestService, times(1)).update(competition);
    }

    @Test
    public void getNextSectionForAlwaysOpenCompetition() {

        ReflectionTestUtils.setField(updater, "isAssessmentStageEnabled", true);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(true)
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationExpressionOfInterestForm form = new ApplicationExpressionOfInterestForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.APPLICATION_ASSESSMENT);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/application-assessment");
    }

    @Test
    public void getNextSectionForNonAlwaysOpenCompetition() {

        ReflectionTestUtils.setField(updater, "isAssessmentStageEnabled", true);

        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .withAlwaysOpen(false)
                .withEnabledForExpressionOfInterest(false)
                .build();

        ApplicationExpressionOfInterestForm form = new ApplicationExpressionOfInterestForm(true);

        String nextSection = updater.getNextSection(form, competition, CompetitionSetupSection.MILESTONES);

        assertThat(nextSection).isEqualTo("redirect:/competition/setup/1/section/milestones");
    }

    @Test
    public void supportsFrom() {
        assertThat(updater.supportsForm(ApplicationExpressionOfInterestForm.class)).isTrue();
        assertThat(updater.supportsForm(CompetitionSetupForm.class)).isFalse();
    }
}
