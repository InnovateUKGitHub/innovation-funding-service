package org.innovateuk.ifs.applicant.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static java.time.ZonedDateTime.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_PROGRESS;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.junit.Assert.assertThat;

/**
 * Testing {@link ApplicantService}
 */
@Rollback
@Transactional
public class ApplicantServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    private static final long USER_ID = 1L;
    private static final long QUESTION_ID = 1L;
    private static final long APPLICATION_ID = 1L;
    private static final long FINANCES_SECTION_ID = 22L;

    @Autowired
    private ApplicantService applicantService;

    private static final ZonedDateTime END_DATE = of(LocalDate.of(2036, 9, 9), LocalTime.of(12, 0), ZoneId.of("Europe/London"));

    @Test
    public void testGetQuestion() {
        loginSteveSmith();

        ServiceResult<ApplicantQuestionResource> result = applicantService.getQuestion(USER_ID, QUESTION_ID, APPLICATION_ID);

        assertThat(result.isSuccess(), equalTo(true));
        ApplicantQuestionResource applicantQuestion = result.getSuccess();

        assertThat(applicantQuestion.getQuestion().getId(), equalTo(QUESTION_ID));
        assertThat(applicantQuestion.getCurrentUser().getId(), equalTo(USER_ID));
        assertThat(applicantQuestion.getApplication().getId(), equalTo(APPLICATION_ID));
        assertThat(applicantQuestion.getApplicantFormInputs().isEmpty(), equalTo(false));
        assertThat(applicantQuestion.getApplicants(), hasItem(applicantQuestion.getCurrentApplicant()));
        assertThat(applicantQuestion.getApplicantQuestionStatuses().isEmpty(), equalTo(false));
    }

    @Test
    public void testGetSection() {
        loginSteveSmith();

        ServiceResult<ApplicantSectionResource> result = applicantService.getSection(USER_ID, FINANCES_SECTION_ID, APPLICATION_ID);

        assertThat(result.isSuccess(), equalTo(true));
        ApplicantSectionResource applicantSection = result.getSuccess();

        assertThat(applicantSection.getSection().getId(), equalTo(FINANCES_SECTION_ID));
        assertThat(applicantSection.getCurrentUser().getId(), equalTo(USER_ID));
        assertThat(applicantSection.getApplication().getId(), equalTo(APPLICATION_ID));
        assertThat(applicantSection.getApplicants(), hasItem(applicantSection.getCurrentApplicant()));
        assertThat(applicantSection.getApplicantParentSection(), notNullValue());
        assertThat(applicantSection.getApplicantChildrenSections().isEmpty(), equalTo(false));
    }

    @Test
    public void testGetApplicantDashboard() {
        loginSteveSmith();

        ServiceResult<ApplicantDashboardResource> result = applicantService.getApplicantDashboard(USER_ID);
        assertThat(result.isSuccess(), equalTo(true));

        ApplicantDashboardResource dashboard = result.getSuccess();
        assertEquals(0, dashboard.getInSetup().size());
        assertEquals(0, dashboard.getEuGrantTransfer().size());
        assertEquals(4, dashboard.getInProgress().size());
        assertEquals(2, dashboard.getPrevious().size());

        DashboardApplicationInProgressResource applicationInProgress = new DashboardApplicationInProgressResourceBuilder()
                .withTitle("A new innovative solution")
                .withCompetitionTitle("Connected digital additive manufacturing")
                .withAssignedToMe(false)
                .withApplicationState(SUBMITTED)
                .withLeadApplicant(true)
                .withDashboardSection(IN_PROGRESS)
                .withApplicationId(5)
                .withAssignedToInterview(false)
                .withApplicationProgress(0)
                .withDaysLeft(DAYS.between(now(), END_DATE))
                .withEndDate(END_DATE)
                .build();
        assertEquals(applicationInProgress, dashboard.getInProgress().get(0));

        DashboardPreviousApplicationResource previousApplication = new DashboardPreviousApplicationResourceBuilder()
                .withAssignedToMe(false)
                .withApplicationState(REJECTED)
                .withLeadApplicant(false)
                .withEndDate(null)
                .withDaysLeft(0)
                .withApplicationProgress(0)
                .withAssignedToInterview(false)
                .withTitle("Using natural gas to heat homes")
                .withApplicationId(4)
                .withCompetitionTitle("Connected digital additive manufacturing")
                .withDashboardSection(PREVIOUS)
                .build();
        assertEquals(previousApplication, dashboard.getPrevious().get(1));
    }

}
