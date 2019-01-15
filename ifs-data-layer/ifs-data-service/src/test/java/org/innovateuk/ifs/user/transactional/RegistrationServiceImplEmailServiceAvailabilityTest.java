package org.innovateuk.ifs.user.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.sil.AbstractEmailServiceAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Rollback;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
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

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@example.com").
                        withPassword("thebspig").
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

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Kieran").
                        withLastName("Worth").
                        withEmail("rollback@worth.com").
                        withPassword("thebspig").
                        build();

                return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                        registrationService.createUser(registrationInfo));
            });
        });

        applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        idpService.setApplicationEventPublisher(applicationEventPublisher);

       regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withServiceAvailableFromEmailService(() -> {

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Kieran").
                        withLastName("Worth").
                        withEmail("rollback@worth.com").
                        withPassword("thebspig").
                        build();

                ServiceResult<UserResource> result = registrationService.createUser(registrationInfo);
                assertTrue(result.isSuccess());
                return result;
            });
        });
    }

    @Test
    public void createOrganisationUserWithCompetitionContextWithEmailServiceUnavailableDoesntLeavePartialDataInDatabase() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withServiceUnavailableFromEmailService(() -> {

                Competition competition = competitionRepository.findByName("Connected digital additive manufacturing").get(0);

                Organisation organisation = getOrganisationForTest();

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@example.com").
                        withPassword("thebspig").
                        build();

                return databaseTestHelper.assertingNoDatabaseChangesOccur(() ->
                        registrationService.createUserWithCompetitionContext(competition.getId(), organisation.getId(), registrationInfo));
            });
        });
    }

    private Organisation getOrganisationForTest() {
        return testService.doWithinTransaction(() -> {
            Organisation organisation = organisationRepository.findOneByName("Empire Ltd");
            Hibernate.initialize(organisation);
            Hibernate.initialize(organisation.getUsers());
            Hibernate.initialize(organisation.getProcessRoles());
            return organisation;
        });
    }
}
