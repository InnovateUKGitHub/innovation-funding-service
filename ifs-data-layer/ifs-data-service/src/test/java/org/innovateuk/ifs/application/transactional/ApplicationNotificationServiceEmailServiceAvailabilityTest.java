package org.innovateuk.ifs.application.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.sil.AbstractEmailServiceAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.function.Consumer;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Tests that this Service will roll back its work if the email service is not available for sending out emails
 */
public class ApplicationNotificationServiceEmailServiceAvailabilityTest extends AbstractEmailServiceAvailabilityIntegrationTest {

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

            withServiceUnavailableFromEmailService(() -> {

                testService.doWithinTransaction(this::loginCompAdmin);

                return databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    ApplicationIneligibleSendResource ineligibleReason = newApplicationIneligibleSendResource().
                            withSubject("An ineligible subject").
                            withMessage("An ineligible reason").
                            build();

                    return applicationNotificationService.informIneligible(ineligibleApplication.getId(), ineligibleReason);
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

        withServiceUnavailableFromEmailService(() -> {

            testService.doWithinTransaction(this::loginCompAdmin);

            return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                    applicationNotificationService.notifyApplicantsByCompetition(fundingDecisionApplicationCompetition));
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

        withServiceUnavailableFromEmailService(() -> {

            setLoggedInUser(newUserResource().
                    withId(submittedApplication.getLeadApplicant().getId()).
                    build());

            return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                    applicationNotificationService.sendNotificationApplicationSubmitted(submittedApplication.getId()));
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
