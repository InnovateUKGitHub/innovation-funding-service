package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.repository.ApplicationExpressionOfInterestConfigRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;

/**
 * Testing {@link ApplicationDashboardService}
 */
@Rollback
@Transactional
public class ApplicationDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private ApplicationDashboardService applicationDashboardService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationExpressionOfInterestConfigRepository applicationExpressionOfInterestConfigRepository;

    private static final DashboardPreviousRowResource EXAMPLE_EXPECTED_DASHBOARD_RESOURCE = new DashboardPreviousApplicationResourceBuilder()
            .withAssignedToMe(false)
            .withApplicationState(REJECTED)
            .withLeadApplicant(true)
            .withEndDate(null)
            .withDaysLeft(0)
            .withApplicationProgress(0)
            .withAssignedToInterview(false)
            .withStartDate(LocalDate.of(2015, 11, 1))
            .withTitle("Using natural gas to heat homes")
            .withApplicationId(4)
            .withCompetitionTitle("Competition 1")
            .withCollaborationLevelSingle(true)
            .withExpressionOfInterest(true)
            .build();

    @Test
    public void testGetApplicantDashboard() {
        loginSteveSmith();
        Long userId = getSteveSmith().getId();




        Application application = applicationRepository.findById(4L).get();
        application.setManageDecisionEmailDate(ZonedDateTime.now());
        application.setDecision(DecisionStatus.UNFUNDED);
        application.setCompetition(newCompetition().withAlwaysOpen(false).build());

        ApplicationExpressionOfInterestConfig applicationExpressionOfInterestConfig =
                ApplicationExpressionOfInterestConfig.builder().
                        application(application).enabledForExpressionOfInterest(true).build();
        application.setApplicationExpressionOfInterestConfig(applicationExpressionOfInterestConfig);
        applicationExpressionOfInterestConfigRepository.save(applicationExpressionOfInterestConfig);
        applicationRepository.save(application);

        List<Application> collect = applicationRepository.findApplicationsForDashboard(userId).stream()
                .filter(Application::isOpen)
                .map(app -> {
                    Competition competition = app.getCompetition();
                    competition.setAlwaysOpen(false);
                    app.setCompetition(competition);
                    return application;
                }).collect(Collectors.toList());
        applicationRepository.saveAll(collect);


        ServiceResult<ApplicantDashboardResource> result = applicationDashboardService.getApplicantDashboard(userId);
        ApplicantDashboardResource dashboard = result.getSuccess();

        assertEquals(0, dashboard.getInSetup().size());
        assertEquals(0, dashboard.getEuGrantTransfer().size());
        assertEquals(5, dashboard.getInProgress().size());
        assertEquals(1, dashboard.getPrevious().size());
        assertTrue( dashboard.getPrevious().get(0).isExpressionOfInterest());


    }

}
