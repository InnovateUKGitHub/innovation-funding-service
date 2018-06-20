package org.innovateuk.ifs.user.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class RegistrationServiceImplRegApiAvailabilityIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

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

    @Test
    public void createUserWithRegistrationApiBeingUnavailableDoesntLeavePartialUserDataInDatabase() {

        regApiHelper.doWithMockIdpRestTemplate(mockIdpRestTemplate -> {

            regApiHelper.setupServiceUnavailableResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

            testService.doWithinTransaction(this::loginSystemRegistrationUser);

            UserRegistrationResource registrationInfo = newUserRegistrationResource().
                    withTitle(Title.Dr).
                    withFirstName("Bob").
                    withLastName("Spiggot").
                    withEmail("thebspig@example.com").
                    withGender(Gender.MALE).
                    withPassword("thebspig").
                    withEthnicity(newEthnicityResource().build()).
                    withRoles(emptyList()).
                    build();

            // assert that we got a failure indicating that the Registration API was not available
            ServiceResult<UserResource> result = registrationService.createUser(registrationInfo);
            assertThat(result.isFailure()).isTrue();
            assertThat(result.getFailure().is(new Error(GENERAL_UNEXPECTED_ERROR, SERVICE_UNAVAILABLE))).isTrue();

            // assert that no partial user data remains in the database
            assertThat(userRepository.findByEmail("thebspig@example.com")).isEmpty();
        });
    }

    @Test
    public void createOrganisationUserWithCompetitionContextWithRegistrationApiUnavailableDoesntLeavePartialDataInDatabase() {

        regApiHelper.doWithMockIdpRestTemplate(mockIdpRestTemplate -> {

            Competition competition = competitionRepository.findByName("Connected digital additive manufacturing").get(0);

            Organisation organisation = getOrganisationForTest();
            int originalUserCount = organisation.getUsers().size();
            int originalProcessRoleCount = organisation.getProcessRoles().size();

            regApiHelper.setupServiceUnavailableResponseExpectationsFromCreateUserCall(mockIdpRestTemplate);

            testService.doWithinTransaction(this::loginSystemRegistrationUser);

            UserResource registrationInfo = newUserResource().
                    withTitle(Title.Dr).
                    withFirstName("Bob").
                    withLastName("Spiggot").
                    withEmail("thebspig@example.com").
                    withGender(Gender.MALE).
                    withPassword("thebspig").
                    build();

            // assert that we got a failure indicating that the Registration API was not available
            ServiceResult<UserResource> result = registrationService.createOrganisationUserWithCompetitionContext(organisation.getId(), competition.getId(), registrationInfo);
            assertThat(result.isFailure()).isTrue();
            assertThat(result.getFailure().is(new Error(GENERAL_UNEXPECTED_ERROR, SERVICE_UNAVAILABLE))).isTrue();

            // assert that no partial user data remains in the database
            assertThat(userRepository.findByEmail("thebspig@example.com")).isEmpty();

            Organisation freshOrganisation = getOrganisationForTest();

            // assert no association data is changed for this organisation
            int freshUserCount = freshOrganisation.getUsers().size();
            int freshProcessRoleCount = freshOrganisation.getProcessRoles().size();
            assertThat(freshUserCount).isEqualTo(originalUserCount);
            assertThat(freshProcessRoleCount).isEqualTo(originalProcessRoleCount);
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
