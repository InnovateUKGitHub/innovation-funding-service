package org.innovateuk.ifs.user.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.sil.AbstractSilAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class RegistrationServiceImplSilAvailabilityIntegrationTest extends AbstractSilAvailabilityIntegrationTest {

    @Autowired
    private RegistrationServiceImpl registrationService;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    public void createOrganisationUserWithEmailServiceUnavailableDoesntLeavePartialDataInDatabase() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

                Organisation organisation = getOrganisationForTest();

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@example.com").
                        withGender(Gender.MALE).
                        withPassword("thebspig").
                        build();

                databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    // assert that we got a failure indicating that the Registration API was not available
                    ServiceResult<UserResource> result = registrationService.createOrganisationUser(organisation.getId(), registrationInfo);
                    assertThat(result.isFailure()).isTrue();
                    verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

                    assertThat(result.getFailure().is(new Error(EMAILS_NOT_SENT_MULTIPLE, SERVICE_UNAVAILABLE))).isTrue();
                });
            });
        });
    }

    @Test
    public void createOrganisationUserWithCompetitionContextWithEmailServiceUnavailableDoesntLeavePartialDataInDatabase() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

                Competition competition = competitionRepository.findByName("Connected digital additive manufacturing").get(0);

                Organisation organisation = getOrganisationForTest();

                regApiHelper.setupSuccessfulResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

                setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

                testService.doWithinTransaction(this::loginSystemRegistrationUser);

                UserResource registrationInfo = newUserResource().
                        withTitle(Title.Dr).
                        withFirstName("Bob").
                        withLastName("Spiggot").
                        withEmail("thebspig@example.com").
                        withGender(Gender.MALE).
                        withPassword("thebspig").
                        build();

                databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    // assert that we got a failure indicating that the Registration API was not available
                    ServiceResult<UserResource> result = registrationService.createOrganisationUserWithCompetitionContext(organisation.getId(), competition.getId(), registrationInfo);
                    assertThat(result.isFailure()).isTrue();
                    assertThat(result.getFailure().is(new Error(EMAILS_NOT_SENT_MULTIPLE, SERVICE_UNAVAILABLE))).isTrue();
                });
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
