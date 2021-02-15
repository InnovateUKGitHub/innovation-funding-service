package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.sil.AbstractEmailServiceAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserCreationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
import static org.junit.Assert.assertTrue;

/**
 * Tests that this Service will roll back its work if the email service is not available for sending out emails
 */
public class RegistrationServiceImplEmailServiceAvailabilityTest extends AbstractEmailServiceAvailabilityIntegrationTest {

    @Autowired
    private RegistrationServiceImpl registrationService;

    @Autowired
    private RestIdentityProviderService idpService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RegistrationApiAvailabilityHelper regApiHelper;

    @Autowired
    private TestService testService;

    @Autowired
    private DatabaseTestHelper databaseTestHelper;

    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void createOrganisationUserWithEmailServiceUnavailableDoesntLeavePartialDataInDatabase() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withServiceUnavailableFromEmailService(() -> {

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserCreationResource registrationInfo = anUserCreationResource().
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@test.com").
                        withPassword("thebspig").
                        withRole(Role.APPLICANT).
                        build();

                return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                        registrationService.createUser(registrationInfo));
            });
        });
    }

    @Test
    @Rollback
    public void createOrganisationUserWithEmailServiceUnavailableRollbacksUserFromLdap() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withServiceUnavailableFromEmailService(() -> {

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserCreationResource registrationInfo = anUserCreationResource().
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@test.com").
                        withPassword("thebspig").
                        withRole(Role.APPLICANT).
                        build();


                return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                        registrationService.createUser(registrationInfo));
            });
        });
    }

    @Test
    @Transactional
    @Rollback
    public void createOrganisationUserWithEmailServiceAvailablePublishesUserEvent() {

        applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        idpService.setApplicationEventPublisher(applicationEventPublisher);

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withServiceAvailableFromEmailService(() -> {

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserCreationResource registrationInfo = anUserCreationResource().
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@test.com").
                        withPassword("thebspig").
                        withRole(Role.APPLICANT).
                        build();

                ServiceResult<UserResource> result = registrationService.createUser(registrationInfo);
                assertTrue(result.isSuccess());
                return result;
            });
        });
    }
}
