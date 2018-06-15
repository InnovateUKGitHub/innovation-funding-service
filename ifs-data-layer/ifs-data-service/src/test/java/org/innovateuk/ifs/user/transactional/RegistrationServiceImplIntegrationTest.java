package org.innovateuk.ifs.user.transactional;

import org.hibernate.Hibernate;
import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.Either;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.Either.left;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class RegistrationServiceImplIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private RegistrationServiceImpl registrationService;

    @Autowired
    private RestIdentityProviderService idpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private TestService testService;

    @Test
    public void registrationApiBeingUnavailableDoesntLeavePartialUserDataInDatabase() {

        doWithMockIdpRestTemplate(mockRestTemplate -> {

            Either<ResponseEntity<Object>, ResponseEntity<Object>> responseFromRegistrationApi =
                    left(new ResponseEntity<>(SERVICE_UNAVAILABLE));

            when(mockRestTemplate.restPostWithEntity(any(), any(), any(), any(), any())).thenReturn(responseFromRegistrationApi);

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
    public void registrationApiBeingUnavailableDoesntLeavePartialOrganisationUserDataInDatabase() {

        doWithMockIdpRestTemplate(mockRestTemplate -> {

            Either<ResponseEntity<Object>, ResponseEntity<Object>> responseFromRegistrationApi =
                    left(new ResponseEntity<>(SERVICE_UNAVAILABLE));

            Organisation organisation = getOrganisationForTest();
            int originalUserCount = organisation.getUsers().size();
            int originalProcessRoleCount = organisation.getProcessRoles().size();

            when(mockRestTemplate.restPostWithEntity(any(), any(), any(), any(), any())).thenReturn(responseFromRegistrationApi);

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
            ServiceResult<UserResource> result = registrationService.createOrganisationUser(organisation.getId(), registrationInfo);
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

    /**
     * Temporarily swaps out the IDP Service's Rest Template out for a mock one during a test run, and restores the
     * original afterwards
     */
    private void doWithMockIdpRestTemplate(Consumer<AbstractRestTemplateAdaptor> testCode) {

        // swap out the real RestTemplate adaptor from the IDP service, so we can simulate a failure in communicating
        // with the registration API
        AbstractRestTemplateAdaptor originalRestTemplate = (AbstractRestTemplateAdaptor) ReflectionTestUtils.getField(idpService, "adaptor");
        AbstractRestTemplateAdaptor mockRestTemplate = mock(AbstractRestTemplateAdaptor.class);
        ReflectionTestUtils.setField(idpService, "adaptor", mockRestTemplate);

        try {

            // run the test code, given the mock rest template
            testCode.accept(mockRestTemplate);
        } finally {

            // and finally swap the original real RestTemplate adaptor back in
            ReflectionTestUtils.setField(idpService, "adaptor", originalRestTemplate);
        }
    }
}
