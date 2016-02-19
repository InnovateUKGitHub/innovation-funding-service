package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest extends BaseUnitTest {
    @InjectMocks
    private RegistrationController registrationController;

    @Before
    public void setUp() {
        super.setup();
        setupUserRoles();

        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setViewResolvers(viewResolver())
                .build();
    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
        ;
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenViewingForm() throws Exception {
        mockMvc.perform(get("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenViewingForm() throws Exception {
        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenSubmittingForm() throws Exception {
        mockMvc.perform(post("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenSubmittingForm() throws Exception {
        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
    }

    @Test
    public void validButAlreadyExistingEmailInputShouldReturnErrorOnEmailField() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        List<UserResource> userResourceList = new ArrayList<>();
        userResourceList.add(newUserResource().build());

        String email = "alreadyexistingemail@test.test";

        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.findUserByEmail(email)).thenReturn(restSuccess(userResourceList));

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", "")
                        .param("retypedPassword", "")
                        .param("title", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("phoneNumber", "")
                        .param("termsAndConditions", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "title"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345")
                        .param("retypedPassword", "123456789012345678901234567890123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void unmatchedPasswordAndRetypePasswordShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345678")
                        .param("retypedPassword", "123456789")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void uncheckedTermsAndConditionsCheckboxShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void validFormInputShouldInitiateCreateUserServiceCall() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();


        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L)).thenReturn(restSuccess(userResource));
        when(userService.findUserByEmail("test@test.test")).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userResource.getEmail())
                        .param("password", userResource.getPassword())
                        .param("retypedPassword", userResource.getPassword())
                        .param("title", userResource.getTitle())
                        .param("firstName", userResource.getFirstName())
                        .param("lastName", userResource.getLastName())
                        .param("phoneNumber", userResource.getPhoneNumber())
                        .param("termsAndConditions", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("userId", "1"))
                .andExpect(view().name("redirect:/application/create/initialize-application/"))
        ;
        // TODO DW - INFUND-1267 - can  we do the below behaviour with Shib SP / IdP?
//        verify(tokenAuthenticationService).addAuthentication(Matchers.isA(HttpServletResponse.class), Matchers.isA(UserResource.class));
    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        Organisation organisation = newOrganisation().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationById(4L)).thenReturn(organisation);
        mockMvc.perform(post("/registration/register?organisationId=4")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }

    @Test
    public void gettingRegistrationPageWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUser().withRolesGlobal(
                        newRole().withName("testrolename").build()
                ).build()
        );

        mockMvc.perform(get("/registration/register?organisationId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"))
        ;

    }

    @Test
    public void postingRegistrationWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUser().withRolesGlobal(
                        newRole().withName("testrolename").build()
                ).build()
        );

        mockMvc.perform(post("/registration/register?organisationId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"))
        ;

    }

    @Test
    public void errorsReturnedInEnvelopeAreAddedToTheModel() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();

        Error error = new Error("errorname", "errordescription", BAD_REQUEST);

        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L)).thenReturn(restFailure(error));
        when(userService.findUserByEmail("test@test.test")).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userResource.getEmail())
                        .param("password", userResource.getPassword())
                        .param("retypedPassword", userResource.getPassword())
                        .param("title", userResource.getTitle())
                        .param("firstName", userResource.getFirstName())
                        .param("lastName", userResource.getLastName())
                        .param("phoneNumber", userResource.getPhoneNumber())
                        .param("termsAndConditions", "1")
        )
                .andExpect(model().hasErrors())
        ;
    }
}