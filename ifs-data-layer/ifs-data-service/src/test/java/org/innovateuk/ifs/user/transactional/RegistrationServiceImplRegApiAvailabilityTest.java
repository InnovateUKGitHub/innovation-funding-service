package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.resource.UserCreationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class RegistrationServiceImplRegApiAvailabilityTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private RegistrationServiceImpl registrationService;

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
    public void createUserWithRegistrationApiBeingUnavailableDoesntLeavePartialUserDataInDatabase() {

        regApiHelper.withMockIdpRestTemplate(mockIdpRestTemplate -> {

            regApiHelper.setupServiceUnavailableResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

            testService.doWithinTransaction(this::loginSystemRegistrationUser);

            UserCreationResource registrationInfo = anUserCreationResource().
                    withFirstName("Bob").
                    withLastName("Spiggot").
                    withEmail("thebspig@example.com").
                    withPassword("thebspig").
                    withRole(APPLICANT).
                    build();

            databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                // assert that we got a failure indicating that the Registration API was not available
                ServiceResult<UserResource> result = registrationService.createUser(registrationInfo);
                assertThat(result.isFailure()).isTrue();
                assertThat(result.getFailure().is(new Error(GENERAL_UNEXPECTED_ERROR, SERVICE_UNAVAILABLE))).isTrue();
            });
        });
    }
}
