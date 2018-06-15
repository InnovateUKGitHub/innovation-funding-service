package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
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
    private TestService testService;

    @Test
    public void registrationApiBeingUnavailableDoesntLeavePartialUserDataInDatabase() {

        // swap out the real RestTemplate adaptor from the IDP service, so we can simulate a failure in communicating
        // with the registration API
        AbstractRestTemplateAdaptor originalRestTemplate = (AbstractRestTemplateAdaptor) ReflectionTestUtils.getField(idpService, "adaptor");
        AbstractRestTemplateAdaptor mockRestTemplate = mock(AbstractRestTemplateAdaptor.class);
        ReflectionTestUtils.setField(idpService, "adaptor", mockRestTemplate);

        try {

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

        } finally {

            // finally swap the original real RestTemplate adaptor back in
            ReflectionTestUtils.setField(idpService, "adaptor", originalRestTemplate);
        }
    }
}
