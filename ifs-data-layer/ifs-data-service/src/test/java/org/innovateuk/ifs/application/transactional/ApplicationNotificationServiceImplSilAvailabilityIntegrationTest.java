package org.innovateuk.ifs.application.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.AbstractSilAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.function.Consumer;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * TODO DW - document this class
 */
public class ApplicationNotificationServiceImplSilAvailabilityIntegrationTest extends AbstractSilAvailabilityIntegrationTest {

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private DatabaseTestHelper databaseTestHelper;

    @Test
    public void informIneligible() throws SQLException {

        withIneligibleApplication(ineligibleApplication -> {

            withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

                setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

                testService.doWithinTransaction(this::loginCompAdmin);

                databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    ApplicationIneligibleSendResource ineligibleReason = newApplicationIneligibleSendResource().
                            withSubject("An ineligible subject").
                            withMessage("An ineligible reason").
                            build();

                    ServiceResult<Void> result = applicationNotificationService.informIneligible(ineligibleApplication.getId(), ineligibleReason);

                    assertThat(result.isFailure()).isTrue();
                    assertThat(result.getFailure().is(new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, SERVICE_UNAVAILABLE))).isTrue();

                    verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
                });
            });
        });
    }

    @Test
    public void notifyApplicantsByCompetition() throws SQLException {

        long fundingDecisionApplicationCompetition = testService.doWithinTransaction(() -> {
            Application approvedApplication = applicationRepository.findByApplicationProcessActivityStateIn(singleton(ApplicationState.APPROVED)).findFirst().get();
            return approvedApplication.getCompetition().getId();
        });

        withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

            setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

            testService.doWithinTransaction(this::loginCompAdmin);

            databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                ServiceResult<Void> result = applicationNotificationService.notifyApplicantsByCompetition(fundingDecisionApplicationCompetition);

                assertThat(result.isFailure()).isTrue();
                assertThat(result.getFailure().is(new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, SERVICE_UNAVAILABLE))).isTrue();

                verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
            });
        });
    }

    @Test
    public void sendNotificationApplicationSubmitted() throws SQLException {

        Application submittedApplication = testService.doWithinTransaction(() -> {
            Application application = applicationRepository.findByApplicationProcessActivityStateIn(singleton(ApplicationState.SUBMITTED)).findFirst().get();
            Hibernate.initialize(application.getCompetition());
            Hibernate.initialize(application.getLeadApplicant());
            return application;
        });

        withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

            setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

            setLoggedInUser(newUserResource().
                    withId(submittedApplication.getLeadApplicant().getId()).
                    build());

            databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                ServiceResult<Void> result = applicationNotificationService.sendNotificationApplicationSubmitted(submittedApplication.getId());

                assertThat(result.isFailure()).isTrue();
                assertThat(result.getFailure().is(new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, SERVICE_UNAVAILABLE))).isTrue();

                verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
            });
        });
    }
    private void withIneligibleApplication(Consumer<Application> consumer) {
        try {

            Application ineligibleApplication = testService.doWithinTransaction(() -> {
                Application submittedApplication = applicationRepository.findByApplicationProcessActivityStateIn(singleton(ApplicationState.SUBMITTED)).findFirst().get();
                submittedApplication.getApplicationProcess().setProcessState(ApplicationState.INELIGIBLE);
                return submittedApplication;
            });

            consumer.accept(ineligibleApplication);

        } finally {

            testService.doWithinTransaction(() -> {
                Application ineligibleApplication = applicationRepository.findByApplicationProcessActivityStateIn(singleton(ApplicationState.INELIGIBLE)).findFirst().get();
                ineligibleApplication.getApplicationProcess().setProcessState(ApplicationState.SUBMITTED);
            });
        }
    }
}
